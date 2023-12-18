locals {
  aws_region = data.aws_caller_identity.current.account_id
  dagger_lambda_jar = "${path.module}/../../target/aws-lambda-dagger-1.0.jar"
  tags = {
    "cits:created_by" = "terraform"
    "cits:project-name" = "search"
    "cits:project-code" = "search"
  }
}