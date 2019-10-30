resource "aws_security_group" "worker" {
  name        = "ab2d-worker-${var.env}"
  description = "Worker security group"
  vpc_id      = var.vpc_id
}

resource "aws_security_group_rule" "api_container_access" {
  type        = "ingress"
  description = "API container access"
  from_port   = var.host_port
  to_port     = var.host_port
  protocol    = "tcp"
  cidr_blocks = var.vpc_cidrs
  security_group_id = aws_security_group.worker.id
}

resource "aws_security_group_rule" "controller_access" {
  type        = "ingress"
  description = "Controller Access"
  from_port   = "-1"
  to_port     = "-1"
  protocol    = "-1"
  source_security_group_id = var.controller_sec_group_id
  security_group_id = aws_security_group.worker.id
}

resource "aws_security_group_rule" "egress_worker" {
  type        = "egress"
  description = "Allow all egress"
  from_port   = "0"
  to_port     = "0"
  protocol    = "-1"
  cidr_blocks = ["0.0.0.0/0"]
  security_group_id = aws_security_group.worker.id
}

#
# Begin EFS configuration
#

resource "aws_security_group" "efs" {
  name        = "ab2d-${var.env}-efs-sg"
  description = "EFS"
  vpc_id      = var.vpc_id
  tags = {
    Name = "AB2D-${upper(var.env)}-EFS-SG"
  }
}

resource "aws_security_group_rule" "efs_ingress" {
  type        = "ingress"
  description = "NFS"
  from_port   = "2049"
  to_port     = "2049"
  protocol    = "tcp"
  source_security_group_id = aws_security_group.worker.id
  security_group_id = aws_security_group.efs.id
}

resource "aws_efs_mount_target" "alpha" {
  file_system_id  = var.efs_id
  subnet_id       = var.alpha
  security_groups = [aws_security_group.efs.id]
}

resource "aws_efs_mount_target" "beta" {
  file_system_id = var.efs_id
  subnet_id      = var.beta
  security_groups = [aws_security_group.efs.id]
}

resource "aws_efs_mount_target" "gamma" {
  file_system_id = var.efs_id
  subnet_id      = var.gamma
  security_groups = [aws_security_group.efs.id]
}

#
# End EFS configuration
#

# resource "aws_ecs_cluster" "ab2d-worker" {
#   name = "ab2d-worker-${var.env}"
# }

resource "aws_ecs_task_definition" "api" {
  family = "worker"
  # LSH SKIP FOR NOW BEGIN
  # volume {
  #   name      = "main"
  #   host_path = "/mnt/efs"
  # }
  # LSH SKIP FOR NOW END
  container_definitions = <<JSON
  [
    {
      "name": "ab2d-worker",
      "image": "${var.docker_repository_url}",
      "essential": true,
      "memory": 2048,
      "portMappings": [
        {
          "containerPort": ${var.container_port},
          "hostPort": ${var.host_port}
        }
      ],
      "logConfiguration": {
        "logDriver": "syslog"
      },
      "healthCheck": null
    }
  ]
JSON
  requires_compatibilities = ["EC2"]
  network_mode = "bridge"
  cpu = 1024
  memory = 2048
  execution_role_arn = "arn:aws:iam::114601554524:role/Ab2dInstanceRole"
}

resource "aws_ecs_service" "worker" {
  name = "ab2d-worker"
  cluster = var.ecs_cluster_id
  task_definition = var.override_task_definition_arn != "" ? var.override_task_definition_arn : aws_ecs_task_definition.api.arn
  desired_count = 5
  launch_type = "EC2"
  scheduling_strategy = "DAEMON"
}

# LSH SKIP FOR NOW BEGIN
# security_groups = [aws_security_group.worker.id,var.enterprise-tools-sec-group-id,var.vpn-private-sec-group-id]
# LSH SKIP FOR NOW END
resource "aws_launch_configuration" "launch_config" {
  name_prefix = "ab2d-worker-${var.env}-"
  image_id = var.ami_id
  instance_type = var.instance_type
  iam_instance_profile = var.iam_instance_profile
  key_name = var.ssh_key_name
  security_groups = [aws_security_group.worker.id]
  user_data = templatefile("${path.module}/userdata.tpl",{ env = var.env, cluster_name = "ab2d-worker-${var.env}", efs_id = var.efs_id })
  lifecycle { create_before_destroy = true }
}

resource "aws_autoscaling_group" "asg" {
  depends_on = ["aws_launch_configuration.launch_config"]
  availability_zones = ["us-east-1a","us-east-1b","us-east-1c","us-east-1d","us-east-1e"]
  name = aws_launch_configuration.launch_config.name
  max_size = var.max_instances
  min_size = var.min_instances
  desired_capacity = var.desired_instances
  health_check_type = "EC2"
  launch_configuration = aws_launch_configuration.launch_config.name
  enabled_metrics = ["GroupTerminatingInstances", "GroupInServiceInstances", "GroupMaxSize", "GroupTotalInstances", "GroupMinSize", "GroupPendingInstances", "GroupDesiredCapacity", "GroupStandbyInstances"]
  vpc_zone_identifier = var.node_subnet_ids
  lifecycle { create_before_destroy = true }

  tags = [
    {
      key = "Name"
      value = "AB2D-WORKER-${upper(var.env)}"
      propagate_at_launch = true
    },
    {
      key = "application"
      value = "AB2D"
      propagate_at_launch = true
    },
    {
      key = "stack"
      value = var.env
      propagate_at_launch = true
    },
    {
      key = "purpose"
      value = "ECS container instance"
      propagate_at_launch = true
    },
    {
      key = "sensitivity"
      value = "Public"
      propagate_at_launch = true
    },
    {
      key = "maintainer"
      value = "federico.rosario@semanticbits.com"
      propagate_at_launch = true
    },
    {
      key = "cpm backup"
      value = "NoBackup"
      propagate_at_launch = true
    },
    {
      key = "purchase_type"
      value = "On-Demand"
      propagate_at_launch = true
    },
    {
      key = "os_license"
      value = "Red Hat Enterprise Linux"
      propagate_at_launch = true
    },
    {
      key = "gold_disk_name"
      value = var.gold_disk_name
      propagate_at_launch = true
    },
    {
      key = "business"
      value = "CMS"
      propagate_at_launch = true
    }
  ]
}

resource "aws_autoscaling_policy" "percent_capacity" {
  name = "percent_capacity"
  scaling_adjustment = var.percent_capacity_increase
  adjustment_type = "PercentChangeInCapacity"
  cooldown = 300
  autoscaling_group_name = aws_autoscaling_group.asg.name
}

resource "aws_autoscaling_policy" "target_cpu" {
  name = "target_CPU"
  autoscaling_group_name = aws_autoscaling_group.asg.name
  policy_type = "TargetTrackingScaling"

  target_tracking_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ASGAverageCPUUtilization"
    }
    target_value = 80.0
  }
}