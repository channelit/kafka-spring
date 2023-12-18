module "us_east_1" {
  source = "../../app"
  providers = {
    aws = aws.use1
  }
  env_name = "dev"
}