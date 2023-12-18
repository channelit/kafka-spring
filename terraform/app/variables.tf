variable "env_name" {
  description = "environment name"
}
variable "log_retention_in_days" {
  default = 5
}
locals {
  name_prefix = "air"
  env = lower(var.env_name)
  name_suffix = var.env_name
}