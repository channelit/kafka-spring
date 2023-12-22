resource "aws_security_group" "air_sg" {
  name   = "${local.name_prefix}-sg-${local.name_suffix}"
  vpc_id = aws_vpc.air_vpc.id
}

resource "aws_kms_key" "air_kms" {
  description = "air"
}

data "aws_iam_policy_document" "air_assume_role" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["firehose.amazonaws.com"]
    }

    actions = ["sts:AssumeRole"]
  }
}

resource "aws_msk_cluster" "air" {
  cluster_name           = "${local.name_prefix}-msk-cluster-${local.name_suffix}"
  kafka_version          = "3.6.0"
  number_of_broker_nodes = 3

  broker_node_group_info {
    instance_type  = "kafka.t3.small"
    #    client_subnets = concat(aws_subnet.air_subnets_public[*].id, aws_subnet.air_subnets_private[*].id)
    client_subnets = aws_subnet.air_subnets_public[*].id
    storage_info {
      ebs_storage_info {
        volume_size = 1000
      }
    }
    security_groups = [aws_security_group.air_sg.id]
  }

  encryption_info {
    encryption_at_rest_kms_key_arn = aws_kms_key.air_kms.arn
  }

  open_monitoring {
    prometheus {
      jmx_exporter {
        enabled_in_broker = true
      }
      node_exporter {
        enabled_in_broker = true
      }
    }
  }

  client_authentication {
    sasl {
      scram = true
    }
  }

  configuration_info {
    arn      = aws_msk_configuration.air_msk_config.arn
    revision = aws_msk_configuration.air_msk_config.latest_revision
  }

  logging_info {
    broker_logs {
      cloudwatch_logs {
        enabled   = true
        log_group = aws_cloudwatch_log_group.air_log.name
      }
      firehose {
        enabled         = true
        delivery_stream = aws_kinesis_firehose_delivery_stream.air_stream.name
      }
      s3 {
        enabled = true
        bucket  = aws_s3_bucket.air_log_s3.id
        prefix  = "logs/msk-"
      }
    }
  }
}

output "zookeeper_connect_string" {
  value = aws_msk_cluster.air.zookeeper_connect_string
}

output "bootstrap_brokers_tls" {
  description = "TLS connection host:port pairs"
  value       = aws_msk_cluster.air.bootstrap_brokers_tls
}

resource "aws_msk_scram_secret_association" "air" {
  cluster_arn     = aws_msk_cluster.air.arn
  secret_arn_list = [aws_secretsmanager_secret.air.arn]

  depends_on = [aws_secretsmanager_secret_version.air]
}

resource "aws_secretsmanager_secret" "air" {
  name       = "AmazonMSK_${local.name_prefix}-sg-${local.name_suffix}5"
  kms_key_id = aws_kms_key.air.key_id
}

resource "aws_kms_key" "air" {
  description = "Example Key for MSK Cluster Scram Secret Association"
}

resource "aws_secretsmanager_secret_version" "air" {
  secret_id     = aws_secretsmanager_secret.air.id
  secret_string = jsonencode({ username = "scott", password = "tiger" })
}

data "aws_iam_policy_document" "air_secret_policy" {
  statement {
    sid    = "AWSKafkaResourcePolicy"
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["kafka.amazonaws.com"]
    }

    actions   = ["secretsmanager:getSecretValue"]
    resources = [aws_secretsmanager_secret.air.arn]
  }
}

resource "aws_secretsmanager_secret_policy" "air" {
  secret_arn = aws_secretsmanager_secret.air.arn
  policy     = data.aws_iam_policy_document.air_secret_policy.json
}

resource "aws_msk_configuration" "air_msk_config" {
  kafka_versions = ["3.6.0"]
  name           = "${local.name_prefix}-msk-config-${local.name_suffix}"

  server_properties = <<PROPERTIES
      allow.everyone.if.no.acl.found=false
      auto.create.topics.enable=true
      default.replication.factor=3
      min.insync.replicas=2
      num.io.threads=8
      num.network.threads=5
      num.partitions=1
      num.replica.fetchers=2
      replica.lag.time.max.ms=30000
      socket.receive.buffer.bytes=102400
      socket.request.max.bytes=104857600
      socket.send.buffer.bytes=102400
      unclean.leader.election.enable=true
      zookeeper.session.timeout.ms=18000
    PROPERTIES
}