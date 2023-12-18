
terraform {
  required_version = ">= 0.13"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.25.0"
    }
  }
}

provider "aws" {
  alias = "use1"
  region = "us-east-1"
  profile = "terraform"
  default_tags {
    tags = {
      Environment = "Dev"
      Owner       = "CITS"
      Application = "AIR"
    }
  }
}