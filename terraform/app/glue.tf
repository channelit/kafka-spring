resource "aws_glue_schema" "air" {
  schema_name       = "${local.name_prefix}-schema-${local.name_suffix}"
  registry_arn      = aws_glue_registry.air.arn
  data_format       = "AVRO"
  compatibility     = "NONE"
  schema_definition = "{\"type\": \"record\", \"name\": \"r1\", \"fields\": [ {\"name\": \"f1\", \"type\": \"int\"}, {\"name\": \"f2\", \"type\": \"string\"} ]}"
}
resource "aws_glue_registry" "air" {
  registry_name = "${local.name_prefix}-registry-${local.name_suffix}"
}