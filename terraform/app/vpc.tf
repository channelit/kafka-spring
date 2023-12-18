resource "aws_vpc" "air_vpc" {
  cidr_block = "192.168.0.0/22"
}

data "aws_availability_zones" "air_azs" {
  state = "available"
}

resource "aws_subnet" "air_subnet_az1" {
  availability_zone = data.aws_availability_zones.air_azs.names[0]
  cidr_block        = "192.168.0.0/24"
  vpc_id            = aws_vpc.air_vpc.id
}

resource "aws_subnet" "air_subnet_az2" {
  availability_zone = data.aws_availability_zones.air_azs.names[1]
  cidr_block        = "192.168.1.0/24"
  vpc_id            = aws_vpc.air_vpc.id
}

resource "aws_subnet" "air_subnet_az3" {
  availability_zone = data.aws_availability_zones.air_azs.names[2]
  cidr_block        = "192.168.2.0/24"
  vpc_id            = aws_vpc.air_vpc.id
}
