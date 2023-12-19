resource "aws_vpc" "air_vpc" {
  cidr_block = "192.168.0.0/16"
  tags       = {
    Name = "${local.name_prefix}-vpc-${local.name_suffix}"
  }
}

data "aws_availability_zones" "air_azs" {
  state = "available"
}

variable "public_subnet_cidrs" {
  type        = list(string)
  description = "Public Subnet CIDR values"
  default     = ["192.168.1.0/24", "192.168.2.0/24", "192.168.3.0/24"]
}

variable "private_subnet_cidrs" {
  type        = list(string)
  description = "Private Subnet CIDR values"
  default     = ["192.168.4.0/24", "192.168.5.0/24", "192.168.6.0/24"]
}

variable "azs" {
  type        = list(string)
  description = "Availability Zones"
  default     = ["us-east-1a", "us-east-1b", "us-east-1c"]
}

resource "aws_subnet" "air_subnets_public" {
  count             = length(var.public_subnet_cidrs)
  availability_zone = element(var.azs, count.index)
  cidr_block        = element(var.public_subnet_cidrs, count.index)
  vpc_id            = aws_vpc.air_vpc.id
  tags              = {
    Name = "${local.name_prefix}-public-subnet-${count.index + 1}-${local.name_suffix}"
  }
}

resource "aws_subnet" "air_subnets_private" {
  count             = length(var.private_subnet_cidrs)
  availability_zone = element(var.azs, count.index)
  cidr_block        = element(var.private_subnet_cidrs, count.index)
  vpc_id            = aws_vpc.air_vpc.id
  tags              = {
    Name = "${local.name_prefix}-private-subnet-${count.index + 1}-${local.name_suffix}"
  }
}


resource "aws_internet_gateway" "air_igw" {
  vpc_id = aws_vpc.air_vpc.id
  tags   = {
    Name = "${local.name_prefix}-vpc-igw-${local.name_suffix}"
  }
}

resource "aws_route_table" "second_rt" {
  vpc_id = aws_vpc.air_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.air_igw.id
  }

  tags = {
    Name = "${local.name_prefix}-rt-${local.name_suffix}"
  }
}

resource "aws_route_table_association" "public_subnet_asso" {
  count          = length(var.public_subnet_cidrs)
  subnet_id      = element(aws_subnet.air_subnets_public[*].id, count.index)
  route_table_id = aws_route_table.second_rt.id
}