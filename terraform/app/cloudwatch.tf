resource "aws_cloudwatch_log_group" "air_log" {
  name = "${local.name_prefix}-msk-logs-${local.name_suffix}"
}

resource "aws_s3_bucket" "air_log_s3" {
  bucket        = "${local.name_prefix}-msk-logs-${local.name_suffix}"
  force_destroy = true
}

resource "aws_s3_bucket_acl" "air_bucket_acl" {
  depends_on = [aws_s3_bucket_ownership_controls.air_bucket_ownership_controls]

  bucket = aws_s3_bucket.air_log_s3.id
  acl    = "private"
}

resource "aws_s3_bucket_ownership_controls" "air_bucket_ownership_controls" {
  bucket = aws_s3_bucket.air_log_s3.id
  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

resource "aws_iam_role" "air_firehose_role" {
  name               = "${local.name_prefix}-firehose-role-${local.name_suffix}"
  assume_role_policy = data.aws_iam_policy_document.air_assume_role.json
}

resource "aws_kinesis_firehose_delivery_stream" "air_stream" {
  name        = "${local.name_prefix}-msk-logs-stream-${local.name_suffix}"
  destination = "extended_s3"
  extended_s3_configuration {
    role_arn   = aws_iam_role.air_firehose_role.arn
    bucket_arn = aws_s3_bucket.air_log_s3.arn
  }

  tags = {
    LogDeliveryEnabled = "placeholder"
  }

  lifecycle {
    ignore_changes = [
      tags["LogDeliveryEnabled"],
    ]
  }
}
