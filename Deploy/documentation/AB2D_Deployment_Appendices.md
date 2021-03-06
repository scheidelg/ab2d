# AB2D Deployment Appendices

## Table of Contents

1. [Appendix A: Access the CMS AWS console](#appendix-a-access-the-cms-aws-console)
1. [Appendix B: View the current gold disk images](#appendix-b-view-the-current-gold-disk-images)
1. [Appendix C: Interact with the developer S3 bucket](#appendix-c-interact-with-the-developer-s3-bucket)
1. [Appendix D: Delete and recreate IAM instance profiles, roles, and policies](#appendix-d-delete-and-recreate-iam-instance-profiles-roles-and-policies)
   * [Delete instance profiles](#delete-instance-profiles)
   * [Delete roles](#delete-roles)
   * [Delete policies not used by IAM users](#delete-policies-not-used-by-iam-users)
1. [Appendix E: Interacting with IAM policy versions](#appendix-e-interacting-with-iam-policy-versions)
1. [Appendix F: Interacting with Elastic Container Repository](#appendix-f-interacting-with-elastic-container-repository)
1. [Appendix G: Generate and test the AB2D website](#appendix-g-generate-and-test-the-ab2d-website)
1. [Appendix H: Get log file from a node](#appendix-h-get-log-file-from-a-node)
1. [Appendix I: Connect to a running container](#appendix-i-connect-to-a-running-container)
1. [Appendix J: Delete and recreate database](#appendix-j-delete-and-recreate-database)
1. [Appendix K: Complete DevOps linting checks](#appendix-k-complete-devops-linting-checks)
   * [Complete terraform linting](#complete-terraform-linting)
   * [Complete python linting](#complete-python-linting)
1. [Appendix L: View existing EUA job codes](#appendix-l-view-existing-eua-job-codes)
1. [Appendix M: Make AB2D static website unavailable](#appendix-m-make-ab2d-static-website-unavailable)
   * [Delete the contents of the websites S3 bucket](#delete-the-contents-of-the-websites-s3-bucket)
   * [Delete the cached website files from the CloudFront edge caches before they expire](#delete-the-cached-website-files-from-the-cloudfront-edge-caches-before-they-expire)
1. [Appendix N: Destroy and redploy API and Worker nodes](#appendix-n-destroy-and-redploy-api-and-worker-nodes)
1. [Appendix O: Destroy complete environment](#appendix-o-destroy-complete-environment)
1. [Appendix P: Display disk space](#appendix-p-display-disk-space)
1. [Appendix Q: Test API using swagger](#appendix-q-test-api-using-swagger)
1. [Appendix R: Update userdata for auto scaling groups through the AWS console](#appendix-r-update-userdata-for-auto-scaling-groups-through-the-aws-console)
1. [Appendix S: Install Ruby on RedHat linux](#appendix-s-install-ruby-on-redhat-linux)
1. [Appendix T: Test getting and decrypting a file from S3](#appendix-t-test-getting-and-decrypting-a-file-from-s3)
1. [Appendix U: Interact with the New Relic infrastructure agent](#appendix-u-interact-with-the-new-relic-infrastructure-agent)
1. [Appendix V: Add a new environment variable for ECS docker containers](#appendix-v-add-a-new-environment-variable-for-ecs-docker-containers)
1. [Appendix W: Launch a base EC2 instance that is created from gold disk AMI](#appendix-w-launch-a-base-ec2-instance-that-is-created-from-gold-disk-ami)
1. [Appendix X: Verify access to the opt-out S3 bucket from sandbox worker nodes](#appendix-x-verify-access-to-the-opt-out-s3-bucket-from-sandbox-worker-nodes)
   * [Test getting a public S3 file using AWS CLI and no sign request](#test-getting-a-public-s3-file-using-aws-cli-and-no-sign-request)
   * [Test downloading a public S3 file using the AWS CLI without credentials](#test-downloading-a-public-s3-file-using-the-aws-cli-without-credentials)
   * [Test downloading a public S3 file using the AWS CLI with the "ab2d-s3-signing" profile](#test-downloading-a-public-s3-file-using-the-aws-cli-with-the-ab2d-s3-signing-profile)
   * [Test interacting with a public S3 file using the AWS Java SDK with environment variables](#test-interacting-with-a-public-s3-file-using-the-aws-java-sdk-with-environment-variables)
   * [Test interacting with a public S3 file on a worker node](#test-interacting-with-a-public-s3-file-on-a-worker-node)
1. [Appendix Y: Test the opt-out process using IntelliJ](#appendix-y-test-the-opt-out-process-using-intellij)
1. [Appendix Z: Test configuration of JVM settings within a container](#appendix-z-test-configuration-of-jvm-settings-within-a-container)
   * [Check the default "heapsize" and "maxram" settings for an api node](#check-the-default-heapsize-and-maxram-settings-for-an-api-node)
   * [Verify JVM parameters for an api node](#verify-jvm-parameters-for-an-api-node)
   * [Check the default "heapsize" and "maxram" settings for a worker node](#check-the-default-heapsize-and-maxram-settings-for-a-worker-node)
   * [Verify JVM parameters for a worker node](#verify-jvm-parameters-for-a-worker-node)
1. [Appendix AA: View CCS Cloud VPN Public IPs](#appendix-aa-view-ccs-cloud-vpn-public-ips)
1. [Appendix BB: Update controller](#appendix-bb-update-controller)
1. [Appendix CC: Fix bad terraform component](#appendix-cc-fix-bad-terraform-component)
1. [Appendix DD: Test running development automation from Jenkins master](#appendix-dd-test-running-development-automation-from-jenkins-master)
1. [Appendix EE: Fix Jenkins reverse proxy error](#appendix-ee-fix-jenkins-reverse-proxy-error)
1. [Appendix FF: Manually add JDK 13 to a node that already has JDK 8](#appendix-ff-manually-add-jdk-13-to-a-node-that-already-has-jdk8)
1. [Appendix GG: Destroy Jenkins agent](#appendix-gg-destroy-jenkins-agent)
1. [Appendix HH: Manual test of Splunk configuration](#appendix-hh-manual-test-of-splunk-configuration)
   * [Request Splunk HEC URL and Splunk HEC Token](#request-splunk-hec-url-and-splunk-hec-token)
   * [Verify or create a "cwlg_lambda_splunk_hec_role" role](#verify-or-create-a-cwlglambdasplunkhecrole-role)
   * [Configure CloudWatch Log agent and onboard \/var\/log\/messages](#configure-cloudwatch-log-agent-and-onboard-varlogmessages)
   * [Verify logging to the \/aws\/ec2\/var\/log\/messages CloudWatch Log Group](#verify-logging-to-the-awsec2varlogmessages-cloudwatch-log-group)
   * [Configure the \/aws\/ec2\/var\/log\/messages CloudWatch Log Group to Splunk HEC Lambda Configuration](#configure-the-awsec2varlogmessages-cloudwatch-log-group-to-splunk-hec-lambda-configuration)
   * [Onboard additional CloudWatch log groups](#onboard-additional-cloudwatch-log-groups)
1. [Appendix II: Get application load balancer access logs](#appendix-ii-get-application-load-balancer-access-logs)

## Appendix A: Access the CMS AWS console

1. Log into Cisco AnyConnect client

   1. Select **Launchpad**
   
   1. Select **Cisco AnyConnect Secure Mobility Client**
   
   1. If a "Cisco AnyConnect Secure Mobility Client is not optimized for your Mac and needs to be updated" message appears, do the following:
      
      1. Select **OK**
   
      1. See the following for more information
   
         > https://support.apple.com/en-us/HT208436
   
   1. Enter the following in the **VPN** dropdown
   
      ```
      cloudvpn.cms.gov
      ```
   
   1. Select **Connect**
   
   1. Enter the following on the "Please enter your username and password" window
   
      - **USERNAME:** {your eua id}
   
      - **PASSWORD:** {password for CloudTamer and Cloud VPN}
   
      - **2nd Password:** {latest 6 number from the google authentication app on mobile phone}
   
   1. Select **OK**
   
   1. Select **Accept**
   
   1. If a "vpndownloader is not optimized for your Mac and needs to be updated" message appears, do the following:
      
      1. Select **OK**
   
      1. See the following for more information
   
         > https://support.apple.com/en-us/HT208436
   
   1. Verify that the VPN successfully connects

1. Log on to CloudTamer

   1. Open Chrome

   1. Enter the following in the address bar

      > https://cloudtamer.cms.gov

   1. Enter the following on the CloudTamer logon page

      - **Username:** {your eua id}

      - **Password:** {password for CloudTamer and Cloud VPN}

      - **Dropdown:** CMS Cloud Services

   1. Select **Log In**

1. Select **Projects** from the leftmost panel

1. Select **Filters**

1. Type the following in the **By Keyword** text box

   ab2d

1. Select **Apply Filters**

1. Select **Account Access** beside "Project AB2D"

1. Select the account
   
   ```
   aws-cms-oit-iusg-acct66 (349849222861)
   ```

1. Select the cloud access role

   
   ```
   West-AB2D
   ```

1. Select **Web Access**

1. Note that the AWS console for the AB2D project should now appear

## Appendix B: View the current gold disk images

1. Open Chrome

1. Enter the following in the address bar

   > https://github.cms.gov/CCSVDC/gold-image/blob/master/ami_latest.txt

## Appendix C: Interact with the developer S3 bucket

1. Set target AWS profile
   
   ```ShellSession
   $ export AWS_PROFILE=ab2d-shared
   ```

1. Verify that the "cms-ab2d-dev" bucket exists

   ```ShellSession
   $ aws --region us-east-1 s3api list-buckets \
     --query "Buckets[?Name=='cms-ab2d-dev'].Name" \
     --output text
   ```

1. Create a text file as an example (optional)

   ```ShellSession
   $ echo "test" > opt-out.txt
   ```
   
1. Put a file in the root of the bucket

   *Example using a file named opt-out.txt exists in the current directory:*
   
   ```ShellSession
   $ aws --region us-east-1 s3api put-object \
     --bucket cms-ab2d-dev \
     --key opt-out.txt \
     --body ./opt-out.txt
   ```

1. List objects in the root of the bucket

   ```ShellSession
   $ aws --region us-east-1 s3api list-objects \
     --bucket cms-ab2d-dev \
     --query 'Contents[].Key' \
     --output text
   ```

1. Get a file from the root of the bucket

   ```ShellSession
   $ aws --region us-east-1 s3api get-object \
     --bucket cms-ab2d-dev \
     --key opt-out.txt \
     ./opt-out-copy.txt
   ```

1. Delete a file from the root of the bucket

   ```ShellSession
   $ aws --region us-east-1 s3api delete-object \
     --bucket cms-ab2d-dev \
     --key opt-out.txt
   ```

1. Verify that the object is gone by listing objects in the root of the bucket again

   ```ShellSession
   $ aws --region us-east-1 s3api list-objects \
     --bucket cms-ab2d-dev \
     --query 'Contents[].Key' \
     --output text
   ```

## Appendix D: Delete and recreate IAM instance profiles, roles, and policies

### Delete instance profiles

1. Set target AWS profile
   
   ```ShellSession
   $ export AWS_PROFILE=ab2d-shared
   ```

1. Detach "Ab2dInstanceRole" from "Ab2dInstanceProfile"

   ```ShellSession
   $ aws iam remove-role-from-instance-profile \
     --role-name Ab2dInstanceRole \
     --instance-profile-name Ab2dInstanceProfile
   ```

1. Delete instance profile

   ```ShellSession
   $ aws iam delete-instance-profile \
     --instance-profile-name Ab2dInstanceProfile
   ```

### Delete roles

1. Set target AWS profile
   
   ```ShellSession
   $ export AWS_PROFILE=ab2d-shared
   ```

1. Detach policies from the "Ab2dManagedRole" role

   ```ShellSession
   $ aws iam detach-role-policy \
     --role-name Ab2dManagedRole \
     --policy-arn arn:aws:iam::349849222861:policy/Ab2dAccessPolicy
   ```

1. Delete "Ab2dManagedRole" role

   ```ShelSession
   $ aws iam delete-role \
     --role-name Ab2dManagedRole
   ```

1. Detach policies from the "Ab2dInstanceRole" role

   ```ShellSession
   $ aws iam detach-role-policy \
     --role-name Ab2dInstanceRole \
     --policy-arn arn:aws:iam::349849222861:policy/Ab2dAssumePolicy
   $ aws iam detach-role-policy \
     --role-name Ab2dInstanceRole \
     --policy-arn arn:aws:iam::349849222861:policy/Ab2dPackerPolicy
   $ aws iam detach-role-policy \
     --role-name Ab2dInstanceRole \
     --policy-arn arn:aws:iam::349849222861:policy/Ab2dS3AccessPolicy
   $ aws iam detach-role-policy \
     --role-name Ab2dInstanceRole \
     --policy-arn arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role
   ```

1. Delete "Ab2dInstanceRole" role

   ```ShelSession
   $ aws iam delete-role \
     --role-name Ab2dInstanceRole
   ```

### Delete policies not used by IAM users

1. Set target AWS profile
   
   ```ShellSession
   $ export AWS_PROFILE=ab2d-shared
   ```

1. Delete "Ab2dAccessPolicy"

   ```ShellSession
   $ aws iam delete-policy \
     --policy-arn arn:aws:iam::349849222861:policy/Ab2dAccessPolicy
   ```

1. Delete "Ab2dAssumePolicy"

   ```ShellSession
   $ aws iam delete-policy \
     --policy-arn arn:aws:iam::349849222861:policy/Ab2dAssumePolicy
   ```

1. Delete "Ab2dPackerPolicy"

   ```ShellSession
   $ aws iam delete-policy \
     --policy-arn arn:aws:iam::349849222861:policy/Ab2dPackerPolicy
   ```

1. Delete "Ab2dS3AccessPolicy"

   ```ShellSession
   $ aws iam delete-policy \
     --policy-arn arn:aws:iam::349849222861:policy/Ab2dS3AccessPolicy
   ```

## Appendix E: Interacting with IAM policy versions

1. List policy versions

   1. Enter the following
   
      *Example of listing policy versions of the "Ab2dAssumePolicy" policy:*
   
      ```ShellSession
      $ aws iam list-policy-versions \
        --policy-arn arn:aws:iam::114601554524:policy/Ab2dAssumePolicy
      ```

   1. Examine the output

      *Example of listing policy versions of the "Ab2dAssumePolicy" policy:*
      
      ```
      {
          "Versions": [
              {
                  "VersionId": "v2",
                  "IsDefaultVersion": true,
                  "CreateDate": "2019-10-11T23:23:54Z"
              },
              {
                  "VersionId": "v1",
                  "IsDefaultVersion": false,
                  "CreateDate": "2019-10-04T19:11:21Z"
              }
          ]
      }
      ```
   
1. Delete a policy version

   *Example of deleting "v1" of the "Ab2dAssumePolicy" policy:*

   ```ShellSession
   $ aws iam delete-policy-version \
     --policy-arn arn:aws:iam::114601554524:policy/Ab2dAssumePolicy \
     --version-id v1
   ```

## Appendix F: Interacting with Elastic Container Repository

1. List ECR repositories

   ```ShellSession
   $ aws --region us-east-1 ecr describe-repositories \
     --query "repositories[*].repositoryName" \
     --output text
   ```

1. Delete an ECR repository

   *Example deleting a repository named "ab2d_sbdemo-dev_api":*
   
   ```ShellSession
   $ aws --region us-east-1 ecr delete-repository \
     --repository-name ab2d_sbdemo-dev_api \
     --force
   ```

## Appendix G: Generate and test the AB2D website

1. Change to the "website" directory

   ```ShellSession
   $ cd ~/code/ab2d/website
   ```

1. Generate and test the website

   1. Ensure required gems are installed

      ```ShellSession
      $ bundle install
      ```

   1. Generate and serve website on the jekyll server

      ```ShellSession
      $ bundle exec jekyll serve
      ```
     
   1. Open Chrome

   1. Enter the following in the address bar
   
      > http://127.0.0.1:4000
      
   1. Verify that the website comes up

   1. Return to the terminal where the jekyll server is running
   
   1. Press **control+c** on the keyboard to stop the Jekyll server

1. Verify the generated site

   1. Note that a "_site" directory was automatically generated when you ran "bundle exec jekyll serve"
   
   1. List the contents of the directory

      ```ShellSession
      $ ls _site
      ```
    
   1. Note that the following two files are used as part of configuring the website within S3

      - index.html

      - 404.html

## Appendix H: Get log file from a node

1. Set target AWS profile

   *Example for "Dev" environment:*

   ```ShellSession
   $ export AWS_PROFILE=ab2d-dev
   ```

   *Example for "Sbx" environment:*

   ```ShellSession
   $ export AWS_PROFILE=ab2d-sbx-sandbox
   ```

1. Set target environment

   *Example for "Dev" environment:*

   ```ShellSession
   $ export TARGET_ENVIRONMENT=ab2d-dev
   ```

   *Example for "Sbx" environment:*

   ```ShellSession
   $ export TARGET_ENVIRONMENT=ab2d-sbx-sandbox
   ```

1. Set controller access variables

   *Example for "Dev" environment:*
   
   ```ShellSession
   $ export CONTROLLER_PUBLIC_IP=52.7.241.208
   $ export SSH_USER_NAME=ec2-user
   ```

   *Example for "Sbx" environment:*
   
   ```ShellSession
   $ export CONTROLLER_PUBLIC_IP=10.242.36.48
   $ export SSH_USER_NAME=ec2-user
   ```

1. Copy the key to the controller

   ```ShellSession
   $ scp -i ~/.ssh/${TARGET_ENVIRONMENT}.pem ~/.ssh/${TARGET_ENVIRONMENT}.pem ${SSH_USER_NAME}@${CONTROLLER_PUBLIC_IP}:~/.ssh
   ```
   
1. Connect to the controller

   *Format:*
   
   ```ShellSession
   $ ssh -i ~/.ssh/${TARGET_ENVIRONMENT}.pem ${SSH_USER_NAME}@${CONTROLLER_PUBLIC_IP}
   ```

1. Set node variables

   *Example for "Dev" environment:*
   
   ```ShellSession
   $ export TARGET_ENVIRONMENT=ab2d-dev
   $ export NODE_PRIVATE_IP=10.242.26.83
   $ export SSH_USER_NAME=ec2-user
   ```

   *Example for "Sbx" environment (api node 1):*
   
   ```ShellSession
   $ export TARGET_ENVIRONMENT=ab2d-sbx-sandbox
   $ export NODE_PRIVATE_IP=10.242.31.193
   $ export SSH_USER_NAME=ec2-user
   ```

   *Example for "Sbx" environment (api node 2):*
   
   ```ShellSession
   $ export TARGET_ENVIRONMENT=ab2d-sbx-sandbox
   $ export NODE_PRIVATE_IP=10.242.31.48
   $ export SSH_USER_NAME=ec2-user
   ```

   *Example for "Sbx" environment (worker node 1):*
   
   ```ShellSession
   $ export TARGET_ENVIRONMENT=ab2d-sbx-sandbox
   $ export NODE_PRIVATE_IP=10.242.31.8
   $ export SSH_USER_NAME=ec2-user
   ```

   *Example for "Sbx" environment (worker node 2):*
   
   ```ShellSession
   $ export TARGET_ENVIRONMENT=ab2d-sbx-sandbox
   $ export NODE_PRIVATE_IP=10.242.31.132
   $ export SSH_USER_NAME=ec2-user
   ```

1. Connect to a node

   *Format:*

   ```ShellSession
   $ ssh -i ~/.ssh/${TARGET_ENVIRONMENT}.pem ${SSH_USER_NAME}@${NODE_PRIVATE_IP}
   ```

1. Switch to root

   ```ShellSession
   $ sudo su
   ```

1. If you want to clear the log to get fresh logging, do the following:

   1. Clear the log

      ```ShellSession
      $ sudo cat /dev/null > /var/log/messages
      ```

   2. Wait a few minutes to get the fresh logging
   
1. Copy "messages" log to ec2-user home directory

   ```ShellSession
   $ cp /var/log/messages /home/ec2-user
   ```

1. Change the ownership on the file

   ```ShellSession
   $ chown ec2-user:ec2-user /home/ec2-user/messages
   ```

1. Exit the root user

   ```ShellSession
   $ exit
   ```

1. Log off node

   ```ShellSession
   $ logout
   ```

1. Copy messages file to the controller

   ```ShellSession
   $ scp -i ~/.ssh/${TARGET_ENVIRONMENT}.pem ${SSH_USER_NAME}@${NODE_PRIVATE_IP}:~/messages .
   ```

1. Log off controller

   ```ShellSession
   $ logout
   ```

1. Copy messages file to development machine

   ```ShellSession
   $ scp -i ~/.ssh/${TARGET_ENVIRONMENT}.pem ${SSH_USER_NAME}@${CONTROLLER_PUBLIC_IP}:~/messages ~/Downloads
   ```

1. Note that the messages file from the remote node can now be found here

   ```
   ~/Downloads/messages
   ```

## Appendix I: Connect to a running container

1. Set target AWS profile

   *Example for "Dev" environment:*

   ```ShellSession
   $ export AWS_PROFILE=ab2d-dev
   ```

   *Example for "Sbx" environment:*

   ```ShellSession
   $ export AWS_PROFILE=ab2d-sbx-sandbox
   ```

1. Set target environment

   *Example for "Dev" environment:*

   ```ShellSession
   $ export TARGET_ENVIRONMENT=ab2d-dev
   ```

   *Example for "Sbx" environment:*

   ```ShellSession
   $ export TARGET_ENVIRONMENT=ab2d-sbx-sandbox
   ```

1. Set controller access variables

   *Example for "Dev" environment:*
   
   ```ShellSession
   $ export CONTROLLER_PUBLIC_IP=52.7.241.208
   $ export SSH_USER_NAME=ec2-user
   ```

1. Copy the key to the controller

   ```ShellSession
   $ scp -i ~/.ssh/${TARGET_ENVIRONMENT}.pem ~/.ssh/${TARGET_ENVIRONMENT}.pem ${SSH_USER_NAME}@${CONTROLLER_PUBLIC_IP}:~/.ssh
   ```
   
1. Connect to the controller

   *Format:*
   
   ```ShellSession
   $ ssh -i ~/.ssh/${TARGET_ENVIRONMENT}.pem ${SSH_USER_NAME}@${CONTROLLER_PUBLIC_IP}
   ```

1. Set node variables

   *Example for "Dev" environment:*
   
   ```ShellSession
   $ export TARGET_ENVIRONMENT=ab2d-dev
   $ export NODE_PRIVATE_IP=10.242.26.231
   $ export SSH_USER_NAME=ec2-user
   ```
   
1. Connect to a node

   *Format:*

   ```ShellSession
   $ ssh -i ~/.ssh/${TARGET_ENVIRONMENT}.pem ${SSH_USER_NAME}@${NODE_PRIVATE_IP}
   ```

1. Connect to a running container

   *Example for connecting to an API container:*

   ```ShellSession
   $ docker exec -it $(docker ps -aqf "name=ecs-api-*" --filter "status=running") /bin/bash
   ```

   *Example for connecting to a worker container:*

   ```ShellSession
   $ docker exec -it $(docker ps -aqf "name=ecs-worker-*" --filter "status=running") /bin/bash
   ```

## Appendix J: Delete and recreate database

1. Set the target AWS profile

   *Example for "Dev" environment:*

   ```ShellSession
   $ export AWS_PROFILE=ab2d-dev
   ```

   *Example for "Sbx" environment:*

   ```ShellSession
   $ export AWS_PROFILE=ab2d-sbx-sandbox
   ```

   *Example for "Impl" environment:*

   ```ShellSession
   $ export AWS_PROFILE=ab2d-east-impl
   ```

1. Set target environment

   *Example for "Dev" environment:*

   ```ShellSession
   $ export TARGET_ENVIRONMENT=ab2d-dev
   ```

   *Example for "Sbx" environment:*

   ```ShellSession
   $ export TARGET_ENVIRONMENT=ab2d-sbx-sandbox
   ```

   *Example for "Impl" environment:*

   ```ShellSession
   $ export TARGET_ENVIRONMENT=ab2d-east-impl
   ```

1. Set controller access variables
   
   ```ShellSession
   $ CONTROLLER_PRIVATE_IP=$(aws --region us-east-1 ec2 describe-instances \
     --filters "Name=tag:Name,Values=ab2d-deployment-controller" \
     --query="Reservations[*].Instances[?State.Name == 'running'].PrivateIpAddress" \
     --output text)
   $ export SSH_USER_NAME=ec2-user
   ```
   
1. Connect to the controller
   
   ```ShellSession
   $ ssh -i ~/.ssh/${TARGET_ENVIRONMENT}.pem ${SSH_USER_NAME}@${CONTROLLER_PRIVATE_IP}
   ```

1. Set target DB environment variables

   *Format:*
   
   ```ShellSession
   $ export MAIN_DB_NAME=postgres
   $ export TARGET_DB_NAME={target database name}
   $ export DB_HOST={database host}
   $ export DB_USER={database user}
   ```

1. Terminate all connections

   ```ShellSession
   $ psql --host  "${DB_HOST}" \
       --username "${DB_USER}" \
       --dbname "${MAIN_DB_NAME}" \
       --command "SELECT pg_terminate_backend(pg_stat_activity.pid) \
         FROM pg_stat_activity \
         WHERE pg_stat_activity.datname = '${TARGET_DB_NAME}' \
         AND pid <> pg_backend_pid();"
   ```

1. Delete the database

   ```ShellSession
   $ dropdb ${TARGET_DB_NAME} --host ${DB_HOST} --username ${DB_USER}
   ```

1. Create database

   ```ShellSession
   $ createdb ${TARGET_DB_NAME} --host ${DB_HOST} --username ${DB_USER}
   ```

## Appendix K: Complete DevOps linting checks

### Complete terraform linting

1. Change to the "Deploy" directory

   ```ShellSession
   $ cd ~/code/ab2d/Deploy
   ```

1. Do a linting check

   ```ShellSession
   $ ./bash/tflint-check.sh
   ```

### Complete python linting

1. Change to the "Deploy" directory

   ```ShellSession
   $ cd ~/code/ab2d/Deploy
   ```

1. Do a linting check

   ```ShellSession
   $ flake8 ./python3 > ~/Downloads/python3-linting.txt
   ```

1. Open the "python3-linting.txt" file and resolve any issues listed

   ```ShellSession
   $ vim ~/Downloads/python3-linting.txt
   ```

## Appendix L: View existing EUA job codes

1. Open Chrome

1. Enter the following in the address bar

   > https://eua.cms.gov/iam/im/pri/
   
1. Log on to EUA

   *Note that you should use the same username and password as you use for Jira and Confluence.*

1. Expand **EUA Info** in the leftmost panel

1. Select **Job Code Listing**

1. Select the following in the **Search for a group** section

   *Example searching for job codes with "SPLUNK" in the name:*
   
   ```
   where  Job Code  = *SPLUNK*	
   ```

## Appendix M: Make AB2D static website unavailable

### Delete the contents of the websites S3 bucket

1. Set target AWS profile
   
   ```ShellSession
   $ export AWS_PROFILE=ab2d-shared
   ```

1. Delete all files from the "cms-ab2d-website" S3 bucket

   ```ShellSession
   $ aws --region us-east-1 s3 rm s3://cms-ab2d-website \
     --recursive
   ```

### Delete the cached website files from the CloudFront edge caches before they expire

1. Set target AWS profile
   
   ```ShellSession
   $ export AWS_PROFILE=ab2d-shared
   ```

1. Invalidate the cloud distribution

   *Format:*
   
   ```ShellSession
   $ aws --region us-east-1 cloudfront create-invalidation \
     --distribution-id {cloudfront distribution id} \
     --paths "/*
   ```

   *Example:*
   
   ```ShellSession
   $ aws --region us-east-1 cloudfront create-invalidation \
     --distribution-id E8P2KHG7IH0TG \
     --paths "/*
   ```

1. Note the output

   *Format:*
   
   ```
   {
       "Location": "https://cloudfront.amazonaws.com/2019-03-26/distribution/{distribution id}/invalidation/{invalidation id}",
       "Invalidation": {
           "Id": "IKBNBOOJNM5OL",
           "Status": "InProgress",
           "CreateTime": "2019-12-16T15:22:16.488Z",
           "InvalidationBatch": {
               "Paths": {
                   "Quantity": 1,
                   "Items": [
                       "/*"
                   ]
               },
               "CallerReference": "cli-{caller reference id}"
           }
       }
   }
   ```

1. Note that the status says "InProgress"

1. Wait a few minutes to all the invalidation process to complete

1. Verify that the invalidation has completed

   1. Enter the following

      *Format:*
      
      ```ShellSession
      $ aws --region us-east-1 cloudfront list-invalidations \
        --distribution-id {cloudfront distribution id}
      ```

      *Example:*
      
      ```ShellSession
      $ aws --region us-east-1 cloudfront list-invalidations \
        --distribution-id E8P2KHG7IH0TG
      ```

   1. Verify that "Status" is "Completed"

      *Example:*
      
      ```
      {
          "InvalidationList": {
              "Items": [
                  {
                      "Id": "IKBNBOOJNM5OL",
                      "CreateTime": "2019-12-16T15:22:16.488Z",
                      "Status": "Completed"
                  }
              ]
          }
      }
      ```

1. Note that the invalidation will remain under the "Invalidations" tab for auditing purposes even if you redeploy the website

## Appendix N: Destroy and redploy API and Worker nodes

1. Set target AWS profile

   *Example for "Dev" environment:*

   ```ShellSession
   $ export AWS_PROFILE=ab2d-dev
   ```

   *Example for "Sbx" environment:*

   ```ShellSession
   $ export AWS_PROFILE=ab2d-sbx-sandbox
   ```

1. Change to the "Deploy" directory

   ```ShellSession
   $ cd ~/code/ab2d/Deploy
   ```

1. Destroy and redploy API and Worker nodes

   *Example for "Dev" environment:*
   
   ```ShellSession
   $ ./bash/redeploy-api-and-worker-nodes.sh \
     --profile=ab2d-dev \
     --environment=ab2d-dev \
     --vpc-id=vpc-0c6413ec40c5fdac3 \
     --ssh-username=ec2-user \
     --owner=842420567215 \
     --ec2-instance-type=m5.xlarge \
     --debug-level=WARN
   ```

   *Example for "Sbx" environment:*
   
   ```ShellSession
   $ ./bash/redeploy-api-and-worker-nodes.sh \
     --profile=ab2d-sbx-sandbox \
     --environment=ab2d-sbx-sandbox \
     --vpc-id=vpc-08dbf3fa96684151c \
     --ssh-username=ec2-user \
     --owner=842420567215 \
     --ec2-instance-type=m5.xlarge \
     --debug-level=WARN
   ```

## Appendix O: Destroy complete environment

1. Change to the deploy directory

   *Format:*
   
   ```ShellSession
   $ cd {code directory}/ab2d/Deploy
   ```

   *Example:*
   
   ```ShellSession
   $ cd ~/code/ab2d/Deploy
   ```
   
1. Destroy the "dev" environment

   *Example to destroy the Dev environment:*
   
   ```ShellSession
   $ ./bash/destroy-environment.sh \
     --environment=ab2d-dev \
     --shared-environment=ab2d-dev-shared
   ```

   *Example to destroy the Dev environment, but preserve the AMIs:*
   
   ```ShellSession
   $ ./bash/destroy-environment.sh \
     --environment=ab2d-dev \
     --shared-environment=ab2d-dev-shared \
     --keep-ami
   ```

1. Destroy the "sbx" environment

   *Example to destroy the Sbx environment:*

   ```ShellSession
   $ ./bash/destroy-environment.sh \
     --environment=ab2d-sbx-sandbox \
     --shared-environment=ab2d-sbx-sandbox-shared
   ```

   *Example to destroy the Sbx environment, but preserve the AMIs:*
   
   ```ShellSession
   $ ./bash/destroy-environment.sh \
     --environment=ab2d-sbx-sandbox \
     --shared-environment=ab2d-sbx-sandbox-shared \
     --keep-ami
   ```

## Appendix P: Display disk space

1. Set target AWS profile

   *Example for "Dev" environment:*

   ```ShellSession
   $ export AWS_PROFILE=ab2d-dev
   ```

   *Example for "Sbx" environment:*

   ```ShellSession
   $ export AWS_PROFILE=ab2d-sbx-sandbox
   ```

1. Set target environment

   *Example for "Dev" environment:*

   ```ShellSession
   $ export TARGET_ENVIRONMENT=ab2d-dev
   ```

   *Example for "Sbx" environment:*

   ```ShellSession
   $ export TARGET_ENVIRONMENT=ab2d-sbx-sandbox
   ```

1. Set controller access variables

   *Example for "Dev" environment:*
   
   ```ShellSession
   $ export CONTROLLER_PUBLIC_IP=52.7.241.208
   $ export SSH_USER_NAME=ec2-user
   ```

   *Example for "Sbx" environment:*
   
   ```ShellSession
   $ export CONTROLLER_PUBLIC_IP=3.93.125.65
   $ export SSH_USER_NAME=ec2-user
   ```

1. Copy the key to the controller

   ```ShellSession
   $ scp -i ~/.ssh/${TARGET_ENVIRONMENT}.pem ~/.ssh/${TARGET_ENVIRONMENT}.pem ${SSH_USER_NAME}@${CONTROLLER_PUBLIC_IP}:~/.ssh
   ```
   
1. Connect to the controller
   
   ```ShellSession
   $ ssh -i ~/.ssh/${TARGET_ENVIRONMENT}.pem ${SSH_USER_NAME}@${CONTROLLER_PUBLIC_IP}
   ```

1. Set node variables

   *Example for "Dev" environment:*
   
   ```ShellSession
   $ export TARGET_ENVIRONMENT=ab2d-dev
   $ export NODE_PRIVATE_IP=10.242.31.196
   $ export SSH_USER_NAME=ec2-user
   ```
   
1. Connect to node

   ```ShellSession
   $ ssh -i ~/.ssh/${TARGET_ENVIRONMENT}.pem ${SSH_USER_NAME}@${NODE_PRIVATE_IP}
   ```

1. Display the amount of disk space available on each file system in a human readable manner

   1. Enter the following

      ```ShellSession
      $ sudo df -h
      ```

   1. Note the output

      ```
      Filesystem                        Size  Used Avail Use% Mounted on
      devtmpfs                          7.6G     0  7.6G   0% /dev
      tmpfs                             7.6G   16K  7.6G   1% /dev/shm
      tmpfs                             7.6G   57M  7.5G   1% /run
      tmpfs                             7.6G     0  7.6G   0% /sys/fs/cgroup
      /dev/mapper/VolGroup00-rootVol     10G  2.4G  7.7G  24% /
      /dev/nvme0n1p1                   1014M  275M  740M  28% /boot
      /dev/mapper/VolGroup00-homeVol    3.0G   33M  3.0G   2% /home
      /dev/mapper/VolGroup00-tmpVol    1014M   34M  981M   4% /tmp
      /dev/mapper/VolGroup00-varVol     6.0G  1.8G  4.3G  30% /var
      /dev/mapper/VolGroup00-vartmpVol 1014M   33M  982M   4% /var/tmp
      /dev/mapper/VolGroup00-logVol     4.0G   43M  4.0G   2% /var/log
      /dev/mapper/VolGroup00-auditVol   4.0G   53M  4.0G   2% /var/log/audit
      overlay                           6.0G  1.8G  4.3G  30% /var/lib/docker/overlay2/806c002b8d5a7e51925854155c7f40808d6501908f17e58560a78e8e70a7d521/merged
      shm                                64M     0   64M   0% /var/lib/docker/containers/831e3b41737ee1174af0a93aa991543831565f3a523f62c35e026661d818bf7d/mounts/shm
      overlay                           6.0G  1.8G  4.3G  30% /var/lib/docker/overlay2/a608f39b66941b6333aaf7b6b1e0346f42f5f1c160a26528a4382da2b4fcb774/merged
      shm                                64M     0   64M   0% /var/lib/docker/containers/617e4a4ec4195e1f47d8e6e4c4e743eeb2d60c26309e42598d318fc53da9dd08/mounts/shm
      tmpfs                             1.6G     0  1.6G   0% /run/user/1000
      ```

## Appendix Q: Test API using swagger

1. Open Chrome

1. Enter the following in the address bar

   > https://confluence.cms.gov/display/AB2D/Step+By+Step+Tutorial

1. Note the following information from this document

   - Client Id

   - Client Password

   - username

   - password

   - OKTA server URL
   
1. Open Postman

1. Create an "ab2d" collection

   1. Select "New"

   1. Select "Collection"

   1. Enter the following on the "CREATE A NEW COLLECTION" page

      - **Name:** ab2d

   1. Select "Create"

1. Add an "retrieve-a-token" request to the "ab2d" collection

   1. Select **...** beside the "ab2d" collection

   1. Select **Add Request**

   1. Enter the following on the "SAVE REQUEST" page

      - **Request name:** retreive-a-token

   1. Select **Save to ab2d**

1. Configure the "retreive-a-token" request

   1. Expand the "ab2d" collection node

   1. Select the following

      ```
      GET retreive-a-token
      ```

   1. Change "GET" to "POST"

   1. Enter the noted OKTA server URL in the "Enter request URL" text box

   1. Select the **Params** tab

   1. Add the following key value pairs

      Key       |Value
      ----------|----------
      grant_type|password
      scope     |openid
      username  |{noted username}
      password  |{noted password}

   1. Note that add parameters now appear as part of the OKTA server URL

   1. Select the **Headers** tab

   1. Add the following key value pairs

      Key         |Value
      ------------|----------
      Content-Type|application/x-www-form-urlencoded
      Accept      |application/json
   
   1. Select the **Authorization** tab

   1. Select "Basic Auth" from the "TYPE" dropdown

   1. Configure the "Authorization" page

      - **Username:** {noted username}

      - **Password:** {noted password}

   1. Select **Send**

   1. Verify that you get an "access_token" within the JSON response

   1. Note the "access_token"

   1. Note that this "access_token" is the JWT access token that you will use within the swagger-ui)

   1. Note that you can get a cURL version of the request by doing the following

      1. Select **Code** to the far right of the tabbed options (Params, Authorization, etc.)

      1. Select **cURL** from the leftmost panel of the "GENERATE CODE SNIPPETS" page

      1. Note the "cURL" statement

1. Open Chrome

1. Enter the "swagger-ui.html" URL for the target environment in the address bar

1. Note the list of API endpoints that are displayed

1. Authorize the endpoints using the JWT access token

   1. Select **Authorize**

   1. Paste the JWT access token value in the **Value** text box using the following format

      *Format:*

      ```
      Bearer {jwt access token}
      ```

   1. Select **Authorize**

   1. Verify that "Authorized" is displayed under "Authorization (apiKey)"

   1. Select **Close**

   1. Verify that all the lock icons are now displayed as locked

   1. Note that locked icons does not mean that you typed in the authorization token correctly

1. Test the "/api/v1/fhir/Patient/$export" API

   1. Select **Export**

   1. Select **GET** beside the "/api/v1/fhir/Patient/$export" API

   1. Note the information about "Parameters" and "Responses"

   1. Select **Try it out**

   1. Configure the "Parameters" as follows

      - **Accept:** application/fhir+json
      
      - **Prefer:** respond-async
      
      - **_outputFormat:** application/fhir_ndjson
      
      - **_type:** ExplanationOfBenefits

   1. Select **Execute**

   1. Note the 202 response which means the export request has started

   1. Note the response

      *Example:*
      
      ```
      cache-control: no-cache, no-store, max-age=0, must-revalidate 
      connection: keep-alive 
      content-length: 0 
      content-location: http://{alb domain}/api/v1/fhir/Job/{job id}/$status 
      date: Tue, 14 Jan 2020 21:09:35 GMT 
      expires: 0 
      pragma: no-cache 
      x-content-type-options: nosniff 
      x-frame-options: DENY 
      x-xss-protection: 1; mode=block 
      ```

   1. Note the job id in the response

   1. Select **GET** again beside the "/api/v1/fhir/Patient/$export" API to collapse the section

1. Test the "/api/v1/fhir/Job/{jobUuid}/$status" API

   1. Select **Status**

   1. Select **GET** beside the "/api/v1/fhir/Job/{jobUuid}/$status" API

   1. Select **Try it out**

   1. Configure the "Parameters" as follows

      - **jobUuid:** {noted job id from the patient export api response}

   1. Select **Execute**
   
   1. Note the 200 response which means the job has completed

   1. Note the following attribute in the output

      *Format:*
      
      ```
      "url": "http://{alb domain}/api/v1/fhir/Job/{jobUuid}/file/{ndjson file}"
      ```

   1. Note the name of the ndjson file in the output

   1. Select **GET** again beside the "/api/v1/fhir/Job/{jobUuid}/$status" API to collapse the section

1. Test the "/api/v1/fhir/Job/{jobUuid}/file/{filename}" API

   1. Select **Download**

   1. Select **GET** beside the "/api/v1/fhir/Job/{jobUuid}/file/{filename}" API

   1. Select **Try it out**

   1. Configure the "Parameters" as follows

      - **Accept:** application/fhir+json

      - **filename:** {noted ndjson file}

      - **jobUuid:** {noted job id from the patient export api response}

   1. Select **Execute**

      > *** TO DO ***: determine why it isn't working

1. Verify EFS is working
   
   1. Set target AWS profile
   
      *Example for "Dev" environment:*
   
      ```ShellSession
      $ export AWS_PROFILE=ab2d-dev
      ```
   
      *Example for "Sbx" environment:*
   
      ```ShellSession
      $ export AWS_PROFILE=ab2d-sbx-sandbox
      ```
   
   1. Set target environment
   
      *Example for "Dev" environment:*
   
      ```ShellSession
      $ export TARGET_ENVIRONMENT=ab2d-dev
      ```
   
      *Example for "Sbx" environment:*
   
      ```ShellSession
      $ export TARGET_ENVIRONMENT=ab2d-sbx-sandbox
      ```
   
   1. Set controller access variables
   
      *Example for "Dev" environment:*
      
      ```ShellSession
      $ export CONTROLLER_PUBLIC_IP=52.7.241.208
      $ export SSH_USER_NAME=ec2-user
      ```
   
      *Example for "Sbx" environment:*
      
      ```ShellSession
      $ export CONTROLLER_PUBLIC_IP=3.93.125.65
      $ export SSH_USER_NAME=ec2-user
      ```
   
   1. Copy the key to the controller
   
      ```ShellSession
      $ scp -i ~/.ssh/${TARGET_ENVIRONMENT}.pem ~/.ssh/${TARGET_ENVIRONMENT}.pem ${SSH_USER_NAME}@${CONTROLLER_PUBLIC_IP}:~/.ssh
      ```
      
   1. Connect to the controller
   
      *Format:*
      
      ```ShellSession
      $ ssh -i ~/.ssh/${TARGET_ENVIRONMENT}.pem ${SSH_USER_NAME}@${CONTROLLER_PUBLIC_IP}
      ```

1. Connect to each host node and docker container

   1. Note that you will want to repeat this section for api nodes 1-2 and worker nodes 1-2

   1. Set node variables
   
      *Example for "Dev" environment (api node 1):*
      
      ```ShellSession
      $ export TARGET_ENVIRONMENT=ab2d-dev
      $ export NODE_PRIVATE_IP=10.242.26.201
      $ export SSH_USER_NAME=ec2-user
      ```

      *Example for "Dev" environment (api node 2):*
      
      ```ShellSession
      $ export TARGET_ENVIRONMENT=ab2d-dev
      $ export NODE_PRIVATE_IP=10.242.26.34
      $ export SSH_USER_NAME=ec2-user
      ```

      *Example for "Dev" environment (worker node 1):*
      
      ```ShellSession
      $ export TARGET_ENVIRONMENT=ab2d-dev
      $ export NODE_PRIVATE_IP=10.242.26.4
      $ export SSH_USER_NAME=ec2-user
      ```

      *Example for "Dev" environment (worker node 2):*
      
      ```ShellSession
      $ export TARGET_ENVIRONMENT=ab2d-dev
      $ export NODE_PRIVATE_IP=10.242.26.196
      $ export SSH_USER_NAME=ec2-user
      ```

      *Example for "Sbx" environment (api node 1):*
      
      ```ShellSession
      $ export TARGET_ENVIRONMENT=ab2d-sbx-sandbox
      $ export NODE_PRIVATE_IP=10.242.31.151
      $ export SSH_USER_NAME=ec2-user
      ```
   
      *Example for "Sbx" environment (api node 2):*
      
      ```ShellSession
      $ export TARGET_ENVIRONMENT=ab2d-sbx-sandbox
      $ export NODE_PRIVATE_IP=10.242.31.120
      $ export SSH_USER_NAME=ec2-user
      ```
   
      *Example for "Sbx" environment (worker node 1):*
      
      ```ShellSession
      $ export TARGET_ENVIRONMENT=ab2d-sbx-sandbox
      $ export NODE_PRIVATE_IP=10.242.31.153
      $ export SSH_USER_NAME=ec2-user
      ```
   
      *Example for "Sbx" environment (worker node 2):*
      
      ```ShellSession
      $ export TARGET_ENVIRONMENT=ab2d-sbx-sandbox
      $ export NODE_PRIVATE_IP=10.242.31.25
      $ export SSH_USER_NAME=ec2-user
      ```
   
   1. Connect to a node
   
      *Format:*
   
      ```ShellSession
      $ ssh -i ~/.ssh/${TARGET_ENVIRONMENT}.pem ${SSH_USER_NAME}@${NODE_PRIVATE_IP}
      ```

   1. Verify the ndjson file for the job under the mounted EFS on the EC2 host

      *Format:*
      
      ```ShellSession
      $ cat /mnt/efs/{job id}/{ndjson file}
      ```

   1. Connect to a running container
   
      *Example for connecting to an API container:*
   
      ```ShellSession
      $ docker exec -it $(docker ps -aqf "name=ecs-api-*" --filter "status=running") /bin/bash
      ```
   
      *Example for connecting to a worker container:*
   
      ```ShellSession
      $ docker exec -it $(docker ps -aqf "name=ecs-worker-*" --filter "status=running") /bin/bash
      ```

   1. Verify the ndjson file for the job under the mounted EFS in the docker container

      *Format:*
      
      ```ShellSession
      $ cat /mnt/efs/{job id}/{ndjson file}
      ```

   1. Exit the docker container

      ```ShellSession
      $ exit
      ```

   1. Exit the EC2 host node

      ```ShellSession
      $ exit
      ```

   1. Make sure that you repeat this section for all host nodes and docker containers

## Appendix R: Update userdata for auto scaling groups through the AWS console

1. Log on to AWS

1. Select **EC2**

1. Select **Launch Configurations** under "AUTO SCALING" in the leftmost panel

1. Modify the API launch configuration

   1. Select the API launch configuration

      *Format:*

      ```
      {environment}-api-{timestamp}
      ```

   1. Select **Copy launch configuarion**

   1. Select **Configure details**

   1. Expand **Advanced Details**

   1. Modify **User data** edit box as desired

   1. Select **Skip to review**

   1. Select **Create launch configuration**

   1. Note that the following is already filled out

      *Format:*
   
      - **First dropdown:** Choose an existing key pair

      - **Select a key pair:** {environment key pair}

   1. Check **I acknowledge...**

   1. Select **Create launch configuration**

   1. Select **Close**

1. Modify the worker launch configuration

   1. Select the worker launch configuration

      *Format:*

      ```
      {environment}-worker-{timestamp}
      ```

   1. Select **Copy launch configuarion**

   1. Select **Configure details**

   1. Expand **Advanced Details**

   1. Modify **User data** edit box as desired

   1. Select **Skip to review**

   1. Select **Create launch configuration**

   1. Note that the following is already filled out

      *Format:*
   
      - **First dropdown:** Choose an existing key pair

      - **Select a key pair:** {environment key pair}

   1. Check **I acknowledge...**

   1. Select **Create launch configuration**

   1. Select **Close**

1. Select **Auto Scaling Groups** under "AUTO SCALING" in the leftmost panel

1. Modify the API auto scaling group

   1. Select the API auto scaling group

      *Format:*

      ```
      {environment}-api-{timestamp}
      ```

   1. Select **Actions**

   1. Select **Edit**

   1. Select the newly created API launch configuration with "Copy" at the end of its name from the **Launch Configuration** dropdown

      *Format:*

      ```
      {environment}-api-{timestamp}Copy
      ```

   1. Select **Save**

1. Modify the worker auto scaling group

   1. Select the worker auto scaling group

      *Format:*

      ```
      {environment}-worker-{timestamp}
      ```

   1. Select **Actions**

   1. Select **Edit**

   1. Select the newly created worker launch configuration with "Copy" at the end of its name from the **Launch Configuration** dropdown

      *Format:*

      ```
      {environment}-worker-{timestamp}Copy
      ```

   1. Select **Save**

1. Terminate the instances that were created by the old launch configuration

1. Wait for the new launch configurations to create new API and worker instances

1. Verify that the API instances received the userdata change

   1. Select **Instances** in the leftmost panel

   1. Select one of the newly created API instances

   1. Select **Actions**

   1. Select **Instance Settings**

   1. Select **View/Change User Data**

   1. Verify the userdata changes that you made are in the **User data** edit box

   1. Select **Cancel**

1. Verify that the worker instances received the userdata change

   1. Select **Instances** in the leftmost panel

   1. Select one of the newly created worker instances

   1. Select **Actions**

   1. Select **Instance Settings**

   1. Select **View/Change User Data**

   1. Verify the userdata changes that you made are in the **User data** edit box

   1. Select **Cancel**

1. Delete the original API launch configuration

   1. Select **Lauch Configurations** under the "AUTO SCALING" section in the leftmost panel

   1. Select the original API launch configuration

      *Format:*

      ```
      {environment}-api-{timestamp}
      ```

   1. Select **Actions**

   1. Select **Delete launch configuration**

   1. Select **Yes, Delete**

1. Delete the original worker launch configuration

   1. Select **Lauch Configurations** under the "AUTO SCALING" section in the leftmost panel

   1. Select the original API launch configuration

      *Format:*

      ```
      {environment}-worker-{timestamp}
      ```

   1. Select **Actions**

   1. Select **Delete launch configuration**

   1. Select **Yes, Delete**

1. Update "tfstate" file

   1. Select **S3**

   1. Navigate to automation bucket path of the changed environment

      *Example for "Dev" environment:*

      ```
      ab2d-dev-automation/ab2d-dev/terraform
      ```
      
      *Example for "Sbx" environment:*

      ```
      ab2d-sbx-sandbox-automation/ab2d-sbx-sandbox/terraform
      ```

   1. Download the "tfstate" file

   1. Note the downloaded file's name changed to the following

      ```
      terraform.json
      ```

   1. Rename the "tfstate" file in S3 to the following

      ```
      terraform.tfstate.backup
      ```

   1. Open the "terraform.json" file

      ```ShellSession
      $ vim ~/Downloads/terraform.json
      ```

   1. Change the API "launch_configuration" line as follows

      ```
      "launch_configuration": "{environment}-api-{timestamp}Copy",     
      ```

   1. Change the worker "launch_configuration" line as follows

      ```
      "launch_configuration": "{environment}-worker-{timestamp}Copy",     
      ```

   1. Save and close the file

   1. Select **S3**

   1. Navigate to automation bucket path of the changed environment

      *Example for "Dev" environment:*

      ```
      ab2d-dev-automation/ab2d-dev/terraform
      ```
      
      *Example for "Sbx" environment:*

      ```
      ab2d-sbx-sandbox-automation/ab2d-sbx-sandbox/terraform
      ```

   1. Upload the modified "terraform.json" to S3

   1. Rename "terraform.json" in S3 to the following

      ```
      terraform.tfstate
      ```

## Appendix S: Install Ruby on RedHat linux

1. Install rbenv dependencies

   ```ShellSession
   $ sudo yum install -y git-core zlib zlib-devel gcc-c++ patch readline \
     readline-devel libyaml-devel libffi-devel openssl-devel make bzip2 \
     autoconf automake libtool bison curl sqlite-devel
   ```

1. Install rbenv and ruby-build

   ```ShellSession
   $ curl -sL https://github.com/rbenv/rbenv-installer/raw/master/bin/rbenv-installer | bash -
   ```

1. Note that will see output that looks similar to this

   ```
   Running doctor script to verify installation...
   Checking for `rbenv' in PATH: which: no rbenv in (/usr/local/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/opt/puppetlabs/bin:/home/ec2-user/.local/bin:/home/ec2-user/bin)
   not found
     You seem to have rbenv installed in `/home/ec2-user/.rbenv/bin', but that
     directory is not present in PATH. Please add it to PATH by configuring
     your `~/.bashrc', `~/.zshrc', or `~/.config/fish/config.fish'.
   ```

1. Add rbenv to path

   ```ShellSession
   $ echo 'export PATH="$HOME/.rbenv/bin:$PATH"' >> ~/.bashrc
   $ echo 'eval "$(rbenv init -)"' >> ~/.bashrc
   $ source ~/.bashrc
   ```

1. Note that you can the determine that latest stable version of Ruby available via rbenv by doing the following

   ```ShellSession
   $ rbenv install -l | grep -v - | tail -1
   ```

1. Note the current pinned version of Ruby on your development machine

   1. Open a new terminal
   
   1. Enter the following on your Mac
   
      ```ShellSession
      $ ruby --version
      ```

   1. Note the base ruby version

      ```
      2.6.5
      ```

   1. Note that you will to use the same version on RedHat because that is the version that was used to test ruby scripts

1. Return to the RedHat node terminal session tab

1. Install the noted version of Ruby

   ```ShellSession
   $ rbenv install 2.6.5
   ```

1. Wait for the installation to complete

   *Note that the installation will take a while. Be patient.*
   
1. Set the global version of Ruby 
   
   ```ShellSession
   $ rbenv global 2.6.5
   ```

1. Verify the Ruby version

   ```ShellSession
   $ ruby --version
   ```

1. Install bundler

   ```ShellSession
   $ gem install bundler
   ```

1. Update Ruby Gems

   ```ShellSession
   $ gem update --system
   ```
   
## Appendix T: Test getting and decrypting a file from S3

1. Copy Gemfile and Rakefile to the controller

   *Example for "Dev" environment:*
   
   ```ShellSession
   $ scp -i ~/.ssh/ab2d-dev.pem ruby/Gemfile ec2-user@52.7.241.208:/tmp
   $ scp -i ~/.ssh/ab2d-dev.pem ruby/Rakefile ec2-user@52.7.241.208:/tmp
   ```

   *Example for "Sbx" environment:*
   
   ```ShellSession
   $ scp -i ~/.ssh/ab2d-sbx-sandbox.pem ruby/Gemfile ec2-user@3.93.125.65:/tmp
   $ scp -i ~/.ssh/ab2d-sbx-sandbox.pem ruby/Rakefile ec2-user@3.93.125.65:/tmp
   ```

1. Connect to the controller

   *Example for "Dev" environment:*
   
   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-dev.pem ec2-user@52.7.241.208
   ```

   *Example for "Sbx" environment:*

   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-sbx-sandbox.pem ec2-user@3.93.125.65
   ```

1. Copy Gemfile and Rakefile to a worker node

   *Example for "Dev" environment:*
   
   ```ShellSession
   $ scp -i ~/.ssh/ab2d-dev.pem /tmp/Gemfile ec2-user@10.242.26.249:/tmp
   $ scp -i ~/.ssh/ab2d-dev.pem /tmp/Rakefile ec2-user@10.242.26.249:/tmp
   ```

   *Example for "Sbx" environment:*
   
   ```ShellSession
   $ scp -i ~/.ssh/ab2d-sbx-sandbox.pem /tmp/Gemfile ec2-user@10.242.31.50:/tmp
   $ scp -i ~/.ssh/ab2d-sbx-sandbox.pem /tmp/Rakefile ec2-user@10.242.31.50:/tmp
   ```

1. Connect to the worker node where the Gemfile and Rakefile were copied

   *Example for "Dev" environment:*
   
   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-dev.pem ec2-user@10.242.26.249
   ```

   *Example for "Sbx" environment:*
   
   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-sbx-sandbox.pem ec2-user@10.242.31.50
   ```

1. Install rbenv dependencies

   ```ShellSession
   $ sudo yum install -y git-core zlib zlib-devel gcc-c++ patch readline \
     readline-devel libyaml-devel libffi-devel openssl-devel make bzip2 \
     autoconf automake libtool bison curl sqlite-devel
   ```

1. Install rbenv and ruby-build

   ```ShellSession
   $ curl -sL https://github.com/rbenv/rbenv-installer/raw/master/bin/rbenv-installer | bash -
   ```

1. Note that will see output that looks similar to this

   ```
   Running doctor script to verify installation...
   Checking for `rbenv' in PATH: which: no rbenv in (/usr/local/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/opt/puppetlabs/bin:/home/ec2-user/.local/bin:/home/ec2-user/bin)
   not found
     You seem to have rbenv installed in `/home/ec2-user/.rbenv/bin', but that
     directory is not present in PATH. Please add it to PATH by configuring
     your `~/.bashrc', `~/.zshrc', or `~/.config/fish/config.fish'.
   ```

1. Add rbenv to path

   ```ShellSession
   $ echo 'export PATH="$HOME/.rbenv/bin:$PATH"' >> ~/.bashrc
   $ echo 'eval "$(rbenv init -)"' >> ~/.bashrc
   $ source ~/.bashrc
   ```

1. Note that you can the determine that latest stable version of Ruby available via rbenv by doing the following

   ```ShellSession
   $ rbenv install -l | grep -v - | tail -1
   ```

1. Note the current pinned version of Ruby on your development machine

   1. Open a new terminal
   
   1. Enter the following on your Mac
   
      ```ShellSession
      $ ruby --version
      ```

   1. Note the base ruby version

      ```
      2.6.5
      ```

   1. Note that you will to use the same version on RedHat because that is the version that was used to test ruby scripts

1. Return to the RedHat node terminal session tab

1. Install the noted version of Ruby

   ```ShellSession
   $ rbenv install 2.6.5
   ```

1. Wait for the installation to complete

   *Note that the installation will take a while. Be patient.*
   
1. Set the global version of Ruby 
   
   ```ShellSession
   $ rbenv global 2.6.5
   ```

1. Verify the Ruby version

   ```ShellSession
   $ ruby --version
   ```

1. Install bundler

   ```ShellSession
   $ gem install bundler
   ```

1. Update Ruby Gems

   ```ShellSession
   $ gem update --system
   ```

1. Change to the "/tmp" directory

   ```ShellSession
   $ cd /tmp
   ```

1. Ensure required gems are installed

   ```ShellSession
   $ bundle install
   ```
   
1. Get keystore from S3 and decrypt it

   *Example for "Dev" environment:*
   
   ```ShellSession
   $ bundle exec rake get_file_from_s3_and_decrypt['./test-file.txt','ab2d-dev-automation']
   ```

   *Example for "Sbx" environment:*

   ```ShellSession
   $ bundle exec rake get_file_from_s3_and_decrypt['./test-file.txt','ab2d-sbx-sandbox-automation']
   ```

   *Example for "Impl" environment:*

   > *** TO DO ***: Run this after deploying to IMPL

   ```ShellSession
   $ bundle exec rake get_file_from_s3_and_decrypt['./test-file.txt','ab2d-east-impl-automation']
   ```

1. Verify that decrypted file is in the "/tmp" directory

   ```ShellSession
   $ cat /tmp/test-file.txt
   ```

1. Create a "bfd-keystore" directory under EFS (if doesn't exist)

   ```ShellSession
   $ sudo mkdir -p /mnt/efs/bfd-keystore
   ```

1. Move file to the "bfd-keystore" directory

   ```ShellSession
   $ sudo mv /tmp/test-file.txt /mnt/efs/bfd-keystore
   ```

## Appendix U: Interact with the New Relic infrastructure agent

1. Connect to any EC2 instance

1. Check the status of the New Relic infrastructure agent

   ```ShellSession
   $ sudo systemctl status newrelic-infra
   ```

1. If New Relic infrastructure agent is stopped, you can start it by running the following command

   ```ShellSession
   $ sudo systemctl start newrelic-infra
   ```

1. If New Relic infrastructure agent is started, you can stop it by running the following command

   ```ShellSession
   $ sudo systemctl stop newrelic-infra
   ```

1. If New Relic infrastructure agent is started, you can stop and restart it by running the following command

   ```ShellSession
   $ sudo systemctl restart newrelic-infra
   ```

## Appendix V: Add a new environment variable for ECS docker containers

1. In order to add a new environment variable for api ECS docker containers, the following files should be changed

   - ~/code/ab2d/Deploy/deploy-ab2d-to-cms.sh

     - add "Create or get '???' secret" section

     - add an error check for a failue to retireve the secret

     - add to "not auto-approved" module.api call

     - add to "auto-approved" module.api call

   - ~/code/ab2d/Deploy/documentation/AB2D_Deployment.md

     - add data entry for '???' secret
   
   - ~/code/ab2d/Deploy/terraform/modules/api/main.tf

     - modify aws_ecs_task_definition

   - ~/code/ab2d/Deploy/terraform/modules/api/variables.tf

     - add variable

   - ~/code/ab2d/Deploy/terraform/environments/ab2d-east-impl/main.tf

     - add variable to the api call

   - ~/code/ab2d/Deploy/terraform/environments/ab2d-east-impl/variables.tf

     - add variable

   - ~/code/ab2d/Deploy/terraform/environments/ab2d-dev/main.tf

     - add variable to the api call
   
   - ~/code/ab2d/Deploy/terraform/environments/ab2d-dev/variables.tf

     - add variable
   
   - ~/code/ab2d/Deploy/terraform/environments/ab2d-sbx-sandbox/main.tf

     - add variable to the api call

   - ~/code/ab2d/Deploy/terraform/environments/ab2d-sbx-sandbox/variables.tf

     - add variable

1. In order to add a new environment variable for worker ECS docker containers, the following files should be changed

   - ~/code/ab2d/Deploy/deploy-ab2d-to-cms.sh

     - add "Create or get '???' secret" section

     - add an error check for a failue to retireve the secret

     - add to "not auto-approved" module.worker call

     - add to "auto-approved" module.worker call

   - ~/code/ab2d/Deploy/documentation/AB2D_Deployment.md

     - add data entry for '???' secret
   
   - ~/code/ab2d/Deploy/terraform/modules/worker/main.tf

     - modify aws_ecs_task_definition

   - ~/code/ab2d/Deploy/terraform/modules/worker/variables.tf

     - add variable

   - ~/code/ab2d/Deploy/terraform/environments/ab2d-east-impl/main.tf

     - add variable to the worker call

   - ~/code/ab2d/Deploy/terraform/environments/ab2d-east-impl/variables.tf

     - add variable

   - ~/code/ab2d/Deploy/terraform/environments/ab2d-dev/main.tf

     - add variable to the worker call
   
   - ~/code/ab2d/Deploy/terraform/environments/ab2d-dev/variables.tf

     - add variable
   
   - ~/code/ab2d/Deploy/terraform/environments/ab2d-sbx-sandbox/main.tf

     - add variable to the worker call

   - ~/code/ab2d/Deploy/terraform/environments/ab2d-sbx-sandbox/variables.tf

     - add variable

## Appendix W: Launch a base EC2 instance that is created from gold disk AMI

1. Open Chrome

1. Log to AWS account

1. Select **EC2**

1. Select **Instances** in the leftmost panel

1. Select **Launch Instance**

1. Enter the following in the "Search for an AMI by entering a search term" text box

   ```
   842420567215
   ```

1. Note that a "results" link will appear in the main part of the page

   *Example:**

   ```
   12 results
   ```

1. Select the "XX results" link (where XX equals the number of results)

   *Example:**

   ```
   12 results
   ```

1. Uncheck **Owned by me** in the leftmost panel

1. Select the most recent "EAST-RH 7-7 Gold Image*" on the "Step 1: Choose an Amazon Machine Image (AMI)" page

   *Example:*

   ```
   EAST-RH 7-7 Gold Image V.1.05 (HVM) 01-23-20 - ami-0f2d8f925de453e46
   Root device type: ebs Virtualization type: hvm Owner: 842420567215 ENA Enabled: Yes
   ```

1. Select the following
    
   ```
   m5.xlarge
   ```

1. Select **Next: Configure Instance Details**

1. Configre the instance details as follows

   - **Number of instances:** 1

   - **Purchasing option:** {unchecked}

   - **Network:** {vpc id} | ab2d-mgmt-east-dev

   - **Subnet:** {subnet id} | ab2d-mgmt-east-dev-public-a | us-east-1a

   - **Auto-assign Public IP:** Enable

   - **Placement group:** {unchecked}

   - **Capacity reservation:** Open

   - **IAM Role:** Ab2dInstanceProfile

   - **CPU Options:** {unchecked}

   - **Shutdown behavior:** Stop

   - **Stop - Hibernate behavior:** {unchecked}

   - **Enable terminate protection:** {checked}

   - **Monitoring:** {unchecked}

   - **EBS-optimized instance:** {checked}

   - **Tenancy:** Shared - Run a shared hardware instance

   - **Elastic Inference:** {unchecked}

1. Select **Next: Add Storage**

1. Change "Size (GiB)" for the "Root" volume

   ```
   250
   ```

1. Select **Next: Add Tags**

1. Select **Next:: Configure Security Group**

1. Configure the "Step 6: Configure Security Group" page as follows

   - **Create a new security group:** {selected}

   - **Security group name:** ab2d-mgmt-east-dev-jenkins-sg

   - **Description:** ab2d-mgmt-east-dev-jenkins-sg

   - **Type:** All traffic

   - **Protocol:** All

   - **Port range:** 0 - 65535

   - **Source:** 10.232.32.0/19

   - **Description:** VPN Access

1. Select **Review and Launch**

1. Review the settings

1. Select **Launch**

1. Select the following from the first dropdown

   ```
   Choose an existing key pair
   ```

1. Select the following from the **Select a key pair** dropdown

   ```
   ab2d-mgmt-east-dev
   ```

1. Check **I acknowledge...**

1. Select **Launch Instances**

1. Select **aws** on the top left of the page

1. Select **EC2**

1. Select **Instances**

1. Wait for the instance to finish starting up with successful status checks

1. SSH into the instance

1. View the available disk devices

   1. Enter the following
   
      ```ShellSession
      $ lsblk
      ```

   1. Note the output

      *Example for an "m5.xlarge" instance:*

      ```
      nvme0n1                  259:0    0  30G  0 disk 
      ├─nvme0n1p1              259:1    0   1G  0 part /boot
      └─nvme0n1p2              259:2    0  29G  0 part 
        ├─VolGroup00-auditVol  253:0    0   4G  0 lvm  /var/log/audit
        ├─VolGroup00-homeVol   253:1    0   3G  0 lvm  /home
        ├─VolGroup00-logVol    253:2    0   4G  0 lvm  /var/log
        ├─VolGroup00-rootVol   253:3    0  10G  0 lvm  /
        ├─VolGroup00-tmpVol    253:4    0   2G  0 lvm  /tmp
        ├─VolGroup00-varVol    253:5    0   5G  0 lvm  /var
        └─VolGroup00-vartmpVol 253:6    0   1G  0 lvm  /var/tmp
      ```

   1. Note the root device in the output

      *Example for an "m5.xlarge" instance:*

      - nvme0n1

   1. Note the existing partitions

      *Example for an "m5.xlarge" instance:*

      - nvme0n1p1

      - nvme0n1p2

1. Create a new partition from unallocated space

   1. View the available disk devices again
   
      ```ShellSession
      $ lsblk
      ```

   1. Note the output

      *Example for an "m5.xlarge" instance:*

      ```
      nvme0n1                  259:0    0  30G  0 disk 
      ├─nvme0n1p1              259:1    0   1G  0 part /boot
      └─nvme0n1p2              259:2    0  29G  0 part 
        ├─VolGroup00-auditVol  253:0    0   4G  0 lvm  /var/log/audit
        ├─VolGroup00-homeVol   253:1    0   3G  0 lvm  /home
        ├─VolGroup00-logVol    253:2    0   4G  0 lvm  /var/log
        ├─VolGroup00-rootVol   253:3    0  10G  0 lvm  /
        ├─VolGroup00-tmpVol    253:4    0   2G  0 lvm  /tmp
        ├─VolGroup00-varVol    253:5    0   5G  0 lvm  /var
        └─VolGroup00-vartmpVol 253:6    0   1G  0 lvm  /var/tmp
      ```

   1. Note the root device in the output

      *Example for an "m5.xlarge" instance:*

      - nvme0n1

   1. Enter the following:
   
      *Format:*
   
      ```ShellSession
      $ sudo fdisk /dev/{root device}
      ```
   
      *Example for an "m5.xlarge" instance:*
   
      ```ShellSession
      $ sudo fdisk /dev/nvme0n1
      ```

   1. Request that the operating system re-reads the partition table

      ```ShellSession
      $ sudo partprobe
      ```

   1. View the available disk devices again
   
      ```ShellSession
      $ lsblk
      ```

   1. Note the output

      *Example for an "m5.xlarge" instance:*
      
      ```
      nvme0n1                  259:0    0  250G  0 disk 
      ├─nvme0n1p1              259:1    0    1G  0 part /boot
      ├─nvme0n1p2              259:2    0   29G  0 part 
      │ ├─VolGroup00-auditVol  253:0    0    4G  0 lvm  /var/log/audit
      │ ├─VolGroup00-homeVol   253:1    0    3G  0 lvm  /home
      │ ├─VolGroup00-logVol    253:2    0    4G  0 lvm  /var/log
      │ ├─VolGroup00-rootVol   253:3    0   10G  0 lvm  /
      │ ├─VolGroup00-tmpVol    253:4    0    2G  0 lvm  /tmp
      │ ├─VolGroup00-varVol    253:5    0    5G  0 lvm  /var
      │ └─VolGroup00-vartmpVol 253:6    0    1G  0 lvm  /var/tmp
      └─nvme0n1p3              259:3    0  220G  0 part 
      ```

   1. Note the newly creating partition

      *Example for an "m5.xlarge" instance:*

      - nvme0n1p3

1. Extend home partition

   1. View the available disk devices again
   
      ```ShellSession
      $ lsblk
      ```

   1. Note the output

      *Example for an "m5.xlarge" instance:*
      
      ```
      nvme0n1                  259:0    0  250G  0 disk 
      ├─nvme0n1p1              259:1    0    1G  0 part /boot
      ├─nvme0n1p2              259:2    0   29G  0 part 
      │ ├─VolGroup00-auditVol  253:0    0    4G  0 lvm  /var/log/audit
      │ ├─VolGroup00-homeVol   253:1    0    3G  0 lvm  /home
      │ ├─VolGroup00-logVol    253:2    0    4G  0 lvm  /var/log
      │ ├─VolGroup00-rootVol   253:3    0   10G  0 lvm  /
      │ ├─VolGroup00-tmpVol    253:4    0    2G  0 lvm  /tmp
      │ ├─VolGroup00-varVol    253:5    0    5G  0 lvm  /var
      │ └─VolGroup00-vartmpVol 253:6    0    1G  0 lvm  /var/tmp
      └─nvme0n1p3              259:3    0  220G  0 part 
      ```

   1. Note the newly creating partition again

      *Example for an "m5.xlarge" instance:*

      - nvme0n1p3

   1. Create physical volume by initializing the partition for use by the Logical Volume Manager (LVM)

      ```ShellSession
      $ sudo pvcreate /dev/nvme0n1p3
      ```

   1. Note the output

      ```
      Physical volume "/dev/nvme0n1p3" successfully created.
      ```

   1. View the available disk devices again
   
      ```ShellSession
      $ lsblk
      ```

   1. Note the output

      *Example for an "m5.xlarge" instance:*
      
      ```
      nvme0n1                  259:0    0  250G  0 disk 
      ├─nvme0n1p1              259:1    0    1G  0 part /boot
      ├─nvme0n1p2              259:2    0   29G  0 part 
      │ ├─VolGroup00-auditVol  253:0    0    4G  0 lvm  /var/log/audit
      │ ├─VolGroup00-homeVol   253:1    0    3G  0 lvm  /home
      │ ├─VolGroup00-logVol    253:2    0    4G  0 lvm  /var/log
      │ ├─VolGroup00-rootVol   253:3    0   10G  0 lvm  /
      │ ├─VolGroup00-tmpVol    253:4    0    2G  0 lvm  /tmp
      │ ├─VolGroup00-varVol    253:5    0    5G  0 lvm  /var
      │ └─VolGroup00-vartmpVol 253:6    0    1G  0 lvm  /var/tmp
      └─nvme0n1p3              259:3    0  220G  0 part 
      ```

   1. Note the volume group

      ```
      VolGroup00
      ```

   1. Add the new physical volume to the volume group

      ```ShellSession
      $ sudo vgextend VolGroup00 /dev/nvme0n1p3
      ```

   1. Note the output

      ```
      Volume group "VolGroup00" successfully extended
      ```

   1. Extend the size of the home logical volume

      ```ShellSession
      $ sudo lvextend -l +100%FREE /dev/mapper/VolGroup00-homeVol
      ```

   1. Note the output

      ```
      Size of logical volume VolGroup00/homeVol changed from <3.00 GiB (767 extents) to 222.99 GiB (57086 extents).
  Logical volume VolGroup00/homeVol successfully resized.
      ```

   1. Expands the existing XFS filesystem

      ```ShellSession
      $ sudo xfs_growfs -d /dev/mapper/VolGroup00-homeVol
      ```

   1. Note the output

      ```
      meta-data=/dev/mapper/VolGroup00-homeVol isize=512    agcount=4, agsize=196352 blks
               =                       sectsz=512   attr=2, projid32bit=1
               =                       crc=1        finobt=0 spinodes=0
      data     =                       bsize=4096   blocks=785408, imaxpct=25
               =                       sunit=0      swidth=0 blks
      naming   =version 2              bsize=4096   ascii-ci=0 ftype=1
      log      =internal               bsize=4096   blocks=2560, version=2
               =                       sectsz=512   sunit=0 blks, lazy-count=1
      realtime =none                   extsz=4096   blocks=0, rtextents=0
      data blocks changed from 785408 to 58456064
      ```

## Appendix X: Verify access to the opt-out S3 bucket from sandbox worker nodes

### Test getting a public S3 file using AWS CLI and no sign request

1. Open a new terminal

1. Change to the abd2 repo code directory

   ```ShellSession
   $ cd ~/code/ab2d
   ```
   
1. Gather opt-out S3 bucket information

   ```ShellSession
   $ cat optout/src/main/resources/application.optout.properties | grep "s3."
   ```

1. Note the output

   ```
   s3.region=${AB2D_S3_REGION:us-east-1}
   s3.bucket=${AB2D_S3_OPTOUT_BUCKET:ab2d-optout-data-dev}
   s3.filename=${AB2D_S3_OPTOUT_FILE:T#EFT.ON.ACO.NGD1800.DPRF.D191029.T1135430}
   ```

1. Delete s3 file (if exists locally)

   ```ShellSession
   $ rm -f /tmp/T#EFT.ON.ACO.NGD1800.DPRF.D191029.T1135430
   ```

1. Note the following

   - the S3 file that we want to download is from a public s3 bucket

   - in order to download a public S3 file without requiring AWS credentials, we need to include the "--no-sign-request" parameter

1. Test getting the file from local machine

   ```ShellSession
   $ aws --region us-east-1 --no-sign-request s3 cp \
     s3://ab2d-optout-data-dev/T#EFT.ON.ACO.NGD1800.DPRF.D191029.T1135430 \
     /tmp/.
   ```

### Test downloading a public S3 file using the AWS CLI without credentials

1. Open a new terminal

1. Test getting a public S3 file

   ```ShellSession
   $ aws --region us-east-1 s3 cp \
     s3://ab2d-optout-data-dev/T#EFT.ON.ACO.NGD1800.DPRF.D191029.T1135430 \
     /tmp/.
   ```

1. Note that this will fail with the following output

   ```
   fatal error: Unable to locate credentials
   ```

### Test downloading a public S3 file using the AWS CLI with the "ab2d-s3-signing" profile

1. Open a new terminal

1. Set AWS profile

   ```ShellSession
   $ export AWS_PROFILE=ab2d-s3-signing
   ```

1. Test getting a public S3 file

   ```ShellSession
   $ aws --region us-east-1 s3 cp \
     s3://ab2d-optout-data-dev/T#EFT.ON.ACO.NGD1800.DPRF.D191029.T1135430 \
     /tmp/.
   ```

### Test interacting with a public S3 file using the AWS Java SDK with environment variables

1. Change to the "s3-client-test" directory

   ```ShellSession
   $ cd ~/code/ab2d/Deploy/java/s3-client-test-workspace/s3-client-test
   ```

1. Build "s3-client-test"

   ```ShellSession
   $ mvn clean package
   ```

1. Set AWS region

   ```ShellSession
   $ export AWS_REGION={'ab2d-s3-signing AWS region' in 1Password}
   ```

1. Set AWS access key id

   ```ShellSession
   $ export AWS_ACCESS_KEY_ID={'ab2d-s3-signing AWS access key id' in 1Password}
   ```

1. Set AWS secret access key

   ```ShellSession
   $ export AWS_SECRET_ACCESS_KEY={'ab2d-s3-signing AWS secret access key' in 1Password}
   ```

1. Test interacting with a public S3 file

   ```ShellSession
   $ java -jar target/s3client-0.0.1-SNAPSHOT.jar
   ```

### Test interacting with a public S3 file on a worker node

1. Change to the "s3-client-test" directory

   ```ShellSession
   $ cd ~/code/ab2d/Deploy/java/s3-client-test-workspace/s3-client-test
   ```

1. Build "s3-client-test"

   ```ShellSession
   $ mvn clean package
   ```

1. Delete existing zipped target directory (if exists)

   ```ShellSession
   $ rm -f target.tgz
   ```

1. Zip up the target directory

   ```ShellSession
   $ tar -czvf target.tgz target
   ```

1. Copy the zipped target directory to a worker node

   *Format:*

   ```
   $ scp -i ~/.ssh/ab2d-dev.pem \
     -o ProxyCommand="ssh ec2-user@{controller private ip} nc {worker private ip} 22" \
	target.tgz \
	ec2-user@{worker private ip}:~
   ```

   *Example for Dev environment:*

   ```
   $ scp -i ~/.ssh/ab2d-dev.pem \
     -o ProxyCommand="ssh ec2-user@10.242.5.190 nc 10.242.26.94 22" \
	target.tgz \
	ec2-user@10.242.26.94:~
   ```

1. Connect to the worker node

   *Format:*
   
   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-dev.pem ec2-user@{worker private ip} \
     -o ProxyCommand="ssh -W %h:%p ec2-user@{controller private ip}"
   ```
   
   *Example for Dev environment:*

   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-dev.pem ec2-user@10.242.26.94 \
     -o ProxyCommand="ssh -W %h:%p ec2-user@10.242.5.190"
   ```

   *Example for Sbx environment:*

   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-sbx-sandbox.pem ec2-user@10.242.31.184 \
     -o ProxyCommand="ssh -W %h:%p ec2-user@10.242.36.49"
   ```

   *Example for Impl environment:*

   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-east-impl.pem ec2-user@10.242.133.14 \
     -o ProxyCommand="ssh -W %h:%p ec2-user@10.242.132.76"
   ```

1. Copy zipped target directory to a worker docker container
   
   ```ShellSession
   $ docker cp target.tgz $(docker ps -aqf "name=ecs-worker-*" --filter "status=running"):/tmp
   ```

1. Connect to a running container

   *Example for connecting to a worker container:*

   ```ShellSession
   $ docker exec -it $(docker ps -aqf "name=ecs-worker-*" --filter "status=running") /bin/bash
   ```

1. Change to the "/tmp" directory

   ```ShellSession
   $ cd /tmp
   ```

1. Delete target directory (if exists)

   ```ShellSession
   $ rm -rf target
   ```

1. Unzip the zipped target file

   ```ShellSession
   $ tar -xzf target.tgz
   ```

1. Run the jar file

   ```ShellSession
   $ java -jar target/s3client-0.0.1-SNAPSHOT.jar
   ```

## Appendix Y: Test the opt-out process using IntelliJ

1. Open a terminal

1. Change to the "ab2d" repo directory

   ```ShellSession
   $ cd ~/code/ab2d
   ```
   
1. Start up the database container

   ```ShellSession
   $ docker-compose up db
   ```

1. Open the ab2d project in IntelliJ

1. Open the following file in IntelliJ

   ```
   optout/src/main/resources/application.optout.properties
   ```

1. Note the cron schedule is currently set to run very hour at minute 0, second 0

   ```
   cron.schedule=0 0 * * * ?
   ```
   
1. Temporarily modify the cron schedule line to run every minute

   ```
   cron.schedule=0 * * * * ?
   ```

1. Add desired debug breakpoints to the following file

   ```
   optout/src/main/java/gov/cms/ab2d/optout/gateway/S3GatewayImpl.java
   ```

1. Select the "Worker" configuration from the dropdown in the upper right of the page

1. Select the debug icon toolbar button

1. Wait for the cron job to trigger so that it reaches the first breakpoint in the "S3GatewayImpl.java" file

1. After testing is complete, be sure to revert the "application.optout.properties" file

   ```ShellSession
   $ git checkout -- optout/src/main/resources/application.optout.properties
   ```

## Appendix Z: Test configuration of JVM settings within a container

### Check the default "heapsize" and "maxram" settings for an api node

1. Connect to a api node

   *Format:*

   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-dev.pem ec2-user@{api private ip} \
     -o ProxyCommand="ssh -W %h:%p ec2-user@{controller private ip}"
   ```

   *Example for Dev environment:*

   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-dev.pem ec2-user@10.242.26.23 \
     -o ProxyCommand="ssh -W %h:%p ec2-user@10.242.5.190"
   ```

   *Example for Sbx environment:*

   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-sbx-sandbox.pem ec2-user@10.242.31.133 \
     -o ProxyCommand="ssh -W %h:%p ec2-user@10.242.36.49"
   ```

1. Connect to the running api container

   ```ShellSession
   $ docker exec -it $(docker ps -aqf "name=ecs-api-*" --filter "status=running") /bin/bash
   ```

1. Get the default "heapsize" and "maxram" settings

   ```ShellSession
   $ java -XX:+PrintFlagsFinal -version | grep -Ei "maxheapsize|maxram"
   ```

1. Note the output

   *Example for Dev environment:*

   ```
   size_t MaxHeapSize           = 2147483648            {product} {ergonomic}
   uint64_t MaxRAM              = 137438953472       {pd product} {default}
   uintx MaxRAMFraction         = 4                     {product} {default}
   double MaxRAMPercentage      = 25.000000             {product} {default}
   size_t SoftMaxHeapSize       = 2147483648         {manageable} {ergonomic}
   ```

1. Exit the container

   ```ShellSession
   $ exit
   ```

1. Exit the api node

   ```ShellSession
   $ exit
   ```

### Verify JVM parameters for an api node

1. Connect to a api node

   *Format:*

   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-dev.pem ec2-user@{api private ip} \
     -o ProxyCommand="ssh -W %h:%p ec2-user@{controller private ip}"
   ```

   *Example for Dev environment:*

   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-dev.pem ec2-user@10.242.26.23 \
     -o ProxyCommand="ssh -W %h:%p ec2-user@10.242.5.190"
   ```

   *Example for Sbx environment:*

   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-sbx-sandbox.pem ec2-user@10.242.31.133 \
     -o ProxyCommand="ssh -W %h:%p ec2-user@10.242.36.49"
   ```

1. Connect to the running api container

   ```ShellSession
   $ docker exec -it $(docker ps -aqf "name=ecs-api-*" --filter "status=running") /bin/bash
   ```

1. Verify JVM parameters for API

   ```ShellSession
   $ jps -lvm | grep api
   ```

1. Note the output

   ```
   1 api-0.0.1-SNAPSHOT.jar -XX:+UseContainerSupport -XX:InitialRAMPercentage=40.0 -XX:MinRAMPercentage=20.0 -XX:MaxRAMPercentage=80.0 -javaagent:/usr/src/ab2d-api/newrelic/newrelic.jar
   ```

1. Exit the container

   ```ShellSession
   $ exit
   ```

1. Exit the api node

   ```ShellSession
   $ exit
   ```

### Check the default "heapsize" and "maxram" settings for a worker node

1. Connect to a worker node

   *Format:*

   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-dev.pem ec2-user@{worker private ip} \
     -o ProxyCommand="ssh -W %h:%p ec2-user@{controller private ip}"
   ```

   *Example for Dev environment:*

   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-dev.pem ec2-user@10.242.26.47 \
     -o ProxyCommand="ssh -W %h:%p ec2-user@10.242.5.190"
   ```

   *Example for Sbx environment:*

   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-sbx-sandbox.pem ec2-user@10.242.31.110 \
     -o ProxyCommand="ssh -W %h:%p ec2-user@10.242.36.49"
   ```

1. Connect to the running worker container

   ```ShellSession
   $ docker exec -it $(docker ps -aqf "name=ecs-worker-*" --filter "status=running") /bin/bash
   ```

1. Get the default "heapsize" and "maxram" settings

   ```ShellSession
   $ java -XX:+PrintFlagsFinal -version | grep -Ei "maxheapsize|maxram"
   ```

1. Note the output

   *Example for Dev environment:*

   ```
   size_t MaxHeapSize           = 2147483648            {product} {ergonomic}
   uint64_t MaxRAM              = 137438953472       {pd product} {default}
   uintx MaxRAMFraction         = 4                     {product} {default}
   double MaxRAMPercentage      = 25.000000             {product} {default}
   size_t SoftMaxHeapSize       = 2147483648         {manageable} {ergonomic}
   ```

1. Exit the container

   ```ShellSession
   $ exit
   ```

1. Exit the worker node

   ```ShellSession
   $ exit
   ```

### Verify JVM parameters for a worker node

1. Connect to a worker node

   *Format:*

   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-dev.pem ec2-user@{worker private ip} \
     -o ProxyCommand="ssh -W %h:%p ec2-user@{controller private ip}"
   ```

   *Example for Dev environment:*

   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-dev.pem ec2-user@10.242.26.47 \
     -o ProxyCommand="ssh -W %h:%p ec2-user@10.242.5.190"
   ```

   *Example for Sbx environment:*

   ```ShellSession
   $ ssh -i ~/.ssh/ab2d-sbx-sandbox.pem ec2-user@10.242.31.110 \
     -o ProxyCommand="ssh -W %h:%p ec2-user@10.242.36.49"
   ```

1. Connect to the running worker container

   ```ShellSession
   $ docker exec -it $(docker ps -aqf "name=ecs-worker-*" --filter "status=running") /bin/bash
   ```

1. Verify JVM parameters for API

   ```ShellSession
   $ jps -lvm | grep worker
   ```

1. Note the output

   ```
   1 worker-0.0.1-SNAPSHOT.jar -XX:+UseContainerSupport -XX:InitialRAMPercentage=40.0 -XX:MinRAMPercentage=20.0 -XX:MaxRAMPercentage=80.0 -javaagent:/usr/src/ab2d-worker/newrelic/newrelic.jar
   ```

1. Exit the container

   ```ShellSession
   $ exit
   ```

1. Exit the api node

   ```ShellSession
   $ exit
   ```

## Appendix AA: View CCS Cloud VPN Public IPs

1. Open Chrome

1. Enter the following in the address bar

   > https://confluence.cms.gov/pages/viewpage.action?spaceKey=AWSOC&title=CCS+Cloud+VPN+Public+IPs

## Appendix BB: Update controller

1. Change to the "Deploy" directory

   ```ShellSession
   $ cd ~/code/ab2d/Deploy
   ```

1. Switch to development AWS profile

   *Example for Dev environment:*
   
   ```ShellSession
   $ export AWS_PROFILE=ab2d-dev
   ```

   *Example for Sbx environment:*
   
   ```ShellSession
   $ export AWS_PROFILE=ab2d-sbx-sandbox
   ```

   *Example for Impl environment:*
   
   ```ShellSession
   $ export AWS_PROFILE=ab2d-east-impl
   ```

1. Set the target environment

   *Example for Dev environment:*

   ```ShellSession
   $ CMS_ENV=ab2d-dev
   ```

   *Example for Sbx environment:*

   ```ShellSession
   $ CMS_ENV=ab2d-sbx-sandbox
   ```

   *Example for Impl environment:*

   ```ShellSession
   $ CMS_ENV=ab2d-east-impl
   ```

1. Set the shared environment

   *Example for Dev environment:*
   
   ```ShellSession
   $ CMS_SHARED_ENV=ab2d-dev-shared
   ```

   *Example for Sbx environment:*
   
   ```ShellSession
   $ CMS_SHARED_ENV=ab2d-sbx-sandbox-shared
   ```

   *Example for Impl environment:*
   
   ```ShellSession
   $ CMS_SHARED_ENV=ab2d-east-impl-shared
   ```

1. Change to the python3 directory

   ```ShellSession
   $ cd python3
   ```

1. Set database secret datetime

   ```ShellSession
   $ DATABASE_SECRET_DATETIME="2020-01-02-09-15-01"
   ```
   
1. Get database user

   ```ShellSession
   $ DATABASE_USER=$(./get-database-secret.py $CMS_ENV database_user $DATABASE_SECRET_DATETIME)
   ```

1. Get database password

   ```ShellSession
   $ DATABASE_PASSWORD=$(./get-database-secret.py $CMS_ENV database_password $DATABASE_SECRET_DATETIME)
   ```

1. Get database name

   ```ShellSession
   $ DATABASE_NAME=$(./get-database-secret.py $CMS_ENV database_name $DATABASE_SECRET_DATETIME)
   ```

1. Get deployer IP address

   ```ShellSession
   $ DEPLOYER_IP_ADDRESS=$(curl ipinfo.io/ip)
   ```

1. Change to the "Deploy" directory

   ```ShellSession
   $ cd ~/code/ab2d/Deploy
   ```

1. Change to the shared environment

   ```ShellSession
   $ cd "terraform/environments/${CMS_SHARED_ENV}"
   ```

1. Verify the changes that will be made

   ```ShellSession
   $ terraform plan \
     --var "db_username=${DATABASE_USER}" \
     --var "db_password=${DATABASE_PASSWORD}" \
     --var "db_name=${DATABASE_NAME}" \
     --var "deployer_ip_address=${DEPLOYER_IP_ADDRESS}" \
     --target module.controller
   ```

1. Update controller

   ```ShellSession
   $ terraform apply \
     --var "db_username=${DATABASE_USER}" \
     --var "db_password=${DATABASE_PASSWORD}" \
     --var "db_name=${DATABASE_NAME}" \
     --var "deployer_ip_address=${DEPLOYER_IP_ADDRESS}" \
     --target module.controller \
     --auto-approve
   ```

## Appendix CC: Fix bad terraform component

1. Note that the following component was failing to refresh when automation was rerun

   *Example of a component that was failing to refresh:*
   
   ```
   module.controller.random_shuffle.public_subnets
   ```
   
1. Change to the "Deploy" directory

   ```ShellSession
   $ cd ~/code/ab2d/Deploy
   ```
   
1. Change to the environment where the existing component is failing to refresh

   *Example for Dev shared environment:*
   
   ```ShellSession
   $ cd terraform/environments/ab2d-dev-shared
   ```

1. Verify that the component is under terraform control

   *Format:*
   
   ```ShellSession
   $ terraform state list | grep {search word}
   ```

   *Example for 'module.controller.random_shuffle.public_subnets':*
   
   ```ShellSession
   $ terraform state list | grep shuffle
   ```

1. Note the terraform reference for the target component

   ```
   module.controller.random_shuffle.public_subnets
   ```

1. Remove the component from terraform control

   ```ShellSession
   $ terraform state rm module.controller.random_shuffle.public_subnets
   ```

1. Rerun the automation, so that the module will be recreated instead of being refreshed

## Appendix DD: Test running development automation from Jenkins master

1. Set the management AWS profile

   ```ShellSession
   $ export AWS_PROFILE=ab2d-mgmt-east-dev
   ```

1. Connect to the Jenkins EC2 instance

   1. Get the private IP address of Jenkins EC2 instance

      ```ShellSession
      $ JENKINS_MASTER_PUBLIC_IP=$(aws --region us-east-1 ec2 describe-instances \
        --filters "Name=tag:Name,Values=ab2d-jenkins-master" \
        --query="Reservations[*].Instances[?State.Name == 'running'].PublicIpAddress" \
        --output text)
      ```

   1. Ensure that you are connected to the Cisco VPN

   1. SSH into the instance using the private IP address

      ```ShellSession
      $ ssh -i ~/.ssh/ab2d-mgmt-east-dev.pem ec2-user@$JENKINS_MASTER_PUBLIC_IP
      ```

1. Switch to the jenkins user

   ```ShellSession
   $ sudo su - jenkins
   ```

1. Create code directory

   ```ShellSession
   $ mkdir -p ~/code
   ```

1. Change to the "code" directory

   ```ShellSession
   $ cd ~/code
   ```

1. Clone the ab2d repo

   ```ShellSession
   $ git clone https://github.com/CMSgov/ab2d.git
   ```

1. Change to the repo directory

   ```ShellSession
   $ cd ~/code/ab2d
   ```

1. Checkout the desired branch

   *Format:*

   ```ShellSession
   $ git checkout {desired branch}
   ```

   *Example:*

   ```ShellSession
   $ git checkout feature/ab2d-573-create-jenkins-scripts-for-automation-hooks
   ```

1. Verify that python3 is working

   1. Change to the "python3" directory

      ```ShellSession
      $ cd ~/code/ab2d/Deploy/python3
      ```

   1. Set the test environment variables

      ```ShellSession
      $ export AWS_PROFILE=ab2d-dev
      $ export CMS_ENV=ab2d-dev
      $ export DATABASE_SECRET_DATETIME=2020-01-02-09-15-01
      ```

   1. Test getting the database user for the development environment

      ```ShellSession
      $ DATABASE_USER=$(./get-database-secret.py $CMS_ENV database_user $DATABASE_SECRET_DATETIME)
      ```

   1. Verify that the database user was retrieved

      ```ShellSession
      $ echo $DATABASE_USER
      ```

   1. Exit the jenkins user so that environment variables can be reset

      ```ShellSession
      $ exit
      ```

1. Connect to the jenkins user

   ```ShellSession
   $ sudo su - jenkins
   ```

1. Change to the "Deploy" directory

   ```ShellSession
   $ cd ~/code/ab2d/Deploy
   ```

1. Set test parameters

   ```ShellSession
   $ export CMS_ENV_PARAM=ab2d-dev
   $ export CMS_ECR_REPO_ENV_PARAM=ab2d-mgmt-east-dev
   $ export REGION_PARAM=us-east-1
   $ export VPC_ID_PARAM=vpc-0c6413ec40c5fdac3
   $ export SSH_USERNAME_PARAM=ec2-user
   $ export EC2_INSTANCE_TYPE_API_PARAM=m5.xlarge
   $ export EC2_INSTANCE_TYPE_WORKER_PARAM=m5.xlarge
   $ export EC2_DESIRED_INSTANCE_COUNT_API_PARAM=1
   $ export EC2_MINIMUM_INSTANCE_COUNT_API_PARAM=1
   $ export EC2_MAXIMUM_INSTANCE_COUNT_API_PARAM=1
   $ export EC2_DESIRED_INSTANCE_COUNT_WORKER_PARAM=1
   $ export EC2_MINIMUM_INSTANCE_COUNT_WORKER_PARAM=1
   $ export EC2_MAXIMUM_INSTANCE_COUNT_WORKER_PARAM=1
   $ export DATABASE_SECRET_DATETIME_PARAM=2020-01-02-09-15-01
   $ export DEBUG_LEVEL_PARAM=WARN
   $ export INTERNET_FACING_PARAM=false
   ```

1. Run application deployment automation

   ```ShellSession
   $ ./bash/deploy-application.sh
   ```

## Appendix EE: Fix Jenkins reverse proxy error

1. Ensure that you are connected to the Cisco VPN

1. Open Chrome

1. Update Jenkins URL for GitHub OAuth application

   1. Lonnie logs onto his GitHub account

   1. Select the GitHub profile icon in the top left of the page

   1. Select **Settings**

   1. Select **Developer settings** from the leftmost panel

   1. Select **OAuth Apps** from the leftmost panel

   1. Select the following OAuth app

      ```
      jenkins-github-authentication
      ```

   1. Change the **Authorization callback URL** to use the desired IP address

      *Example of using the private IP address:**

      > http://{jenkins master private ip address}:8080/securityRealm/finishLogin

      *Example of using the public IP address:**

      > http://{jenkins master public ip address}:8080/securityRealm/finishLogin


   1. If you changed the "Authorization callback URL", select **Update application**

1. Enter the Jenkins master URL based on the IP address used to configure the "Authorization callback URL"

   *Format:*

   > http://{jenkins ip address used for github authentication}:8080

   *Example if GitHub integration is configured with private IP address:*

   > http://{jenkins master private ip}:8080

   *Example if GitHub integration is configured with public IP address:*

   > http://{jenkins master public ip}:8080

1. Select **Manage Jenkins**

1. Note that if you see the following error, these instructions will fix the issue

   ```
   It appears that your reverse proxy set up is broken.
   ```

1. Select **Configure System**

1. Type the following in the **Jenkins URL** text box under the "Jenkins Location" section

   *Format:*

   > http://{jenkins ip address used for github authentication}:8080

   *Example if GitHub integration is configured with private IP address:*

   > http://{jenkins master private ip}:8080

   *Example if GitHub integration is configured with public IP address:*

   > http://{jenkins master public ip}:8080

1. Select **Apply**

1. Select **Save**

1. Select **Manage Jenkins** again

1. Note that the reverse proxy warning no longer appears

## Appendix FF: Manually add JDK 13 to a node that already has JDK 8

1. Connect to desired node

1. Install JDK 13

   ```ShellSession
   $ sudo yum install java-13-openjdk-devel -y
   ```

1. Get JDK 13 java binary

   ```ShellSession
   $ JAVA_13=$(alternatives --display java | grep family | grep java-13-openjdk | cut -d' ' -f1)
   ```
   
1. Set java binary to JDK 13

   ```ShellSession
   $ sudo alternatives --set java $JAVA_13
   ```

1. Get JDK 13 javac binary

   ```ShellSession
   $ JAVAC_13=$(alternatives --display javac | grep family | grep java-13-openjdk | cut -d' ' -f1)
   ```
   
1. Set javac binary to JDK 13

   ```ShellSession
   $ sudo alternatives --set javac $JAVAC_13
   ```

## Appendix GG: Destroy Jenkins agent

1. Change to the "Deploy" directory

   ```ShellSession
   $ cd ~/code/ab2d/Deploy
   ```

1. Change to the shared manegment environment

   ```ShellSession
   $ cd terraform/environments/ab2d-mgmt-east-dev-shared
   ```

1. Destroy the Jenkins agent

   ```ShellSession
   $ terraform destroy \
     --target module.jenkins_agent \
     --auto-approve
   ```

## Appendix HH: Manual test of Splunk configuration

### Request Splunk HEC URL and Splunk HEC Token

> *** TO DO ***

1. Open Chrome

1. Enter the following in the address bar

   > https://jiraent.cms.gov/servicedesk/customer/portal/13

1. Configure the request as follows

   - **Summary:** Request Splunk HEC URL and Splunk HEC Token for AB2D project

   - **Project Name:** Project 058 BCDA

   - **Account Alias:** None

   - **Service Category:** Central Logging

   - **Task:** Onboarding and Data Ingestion

   - **Description:**

     ```
     Hello,
     
     I am requesting a Splunk HEC URL and Splunk HEC Token.
     
     Note that this is for the AB2D project.
     
     Thanks for your consideration.
     
     Lonnie Hanekamp
     ```

   - **Severity:** Minimal

   - **Urgency:** Low

   - **Reported Source:** Self Service

   - **Requested Due Date:** {3 working days from now}

1. Select **Create**

### Verify or create a "cwlg_lambda_splunk_hec_role" role

1. Open Chrome

1. Log on to the development AWS account

1. Select "IAM"

1. Select **Roles**

1. Determine if the "cwlg_lambda_splunk_hec_role" role alredy exists

   1. Type the following in the **Search** text box under "Create role"

      ```
      cwlg_lambda_splunk_hec_role
      ```

   1. If the role already exists, jump to the following section

      [Configure CloudWatch Log agent and onboard \/var\/log\/messages](#configure-cloudwatch-log-agent-and-onboard-varlogmessages)
      
1. Select **Create role**

1. Select the **AWS service** tab

1. Select **Lambda**

1. Select **Next: Permissions**

1. Type the following in the **Search** text box

   ```
   AWSLambdaBasicExecutionRole
   ```

1. Select the checkbox beside "AWSLambdaBasicExecutionRole"

1. Select **Next: Tags**

1. Select **Next: Review**

1. Configure the "Review" page as follows

   - **Role name:** cwlg_lambda_splunk_hec_role

1. Select **Create role**

### Configure CloudWatch Log agent and onboard \/var\/log\/messages

1. Connect to an API node in development

   1. Ensure that you are connected to the Cisco VPN

   1. Set the dev AWS profile

      ```ShellSession
      $ export AWS_PROFILE=ab2d-dev
      ```

   1. Connect to the development controller

      ```ShellSession
      $ ssh -i ~/.ssh/${AWS_PROFILE}.pem ec2-user@$(aws \
        --region us-east-1 ec2 describe-instances \
        --filters "Name=tag:Name,Values=ab2d-deployment-controller" \
        --query="Reservations[*].Instances[?State.Name == 'running'].PrivateIpAddress" \
        --output text)
      ```

   1. Connect to the desired API node

      *Example for connecting to the first API node:*

      ```ShellSession
      $ ssh ec2-user@$(./list-api-instances.sh \
        | grep 10. \
        | awk '{print $2}' \
        | head -n 1)
      ```

1. Switch to the root user

   ```ShellSession
   $ sudo su
   ```

1. Verify existing logs prior to changes

   OS Level Logging                        |Exists|CloudWatch Log Group
   ----------------------------------------|------|------------------------------------------------
   /var/log/amazon/ssm/amazon-ssm-agent.log|yes   |/aws/ec2/var/log/amazon/ssm/amazon-ssm-agent.log
   /var/log/audit/audit.log                |yes   |/aws/ec2/var/log/audit/audit.log
   /var/log/awslogs.log                    |no    |/aws/ec2/var/log/awslogs.log
   /var/log/cloud-init-output.log          |yes   |/aws/ec2/var/log/cloud-init-output.log
   /var/log/cloud-init.log                 |yes   |/aws/ec2/var/log/cloud-init.log
   /var/log/cron                           |yes   |/aws/ec2/var/log/cron
   /var/log/dmesg                          |yes   |/aws/ec2/var/log/dmesg
   /var/log/maillog                        |yes   |/aws/ec2/var/log/maillog
   /var/log/messages                       |yes   |/aws/ec2/var/log/messages
   /var/log/secure                         |yes   |/aws/ec2/var/log/secure
   /var/opt/ds_agent/diag/ds_agent-err.log |yes   |/aws/ec2/var/opt/ds_agent/diag/ds_agent-err.log
   /var/opt/ds_agent/diag/ds_agent.log     |yes   |/aws/ec2/var/opt/ds_agent/diag/ds_agent.log
   /var/opt/ds_agent/diag/ds_am.log        |yes   |/aws/ec2/var/opt/ds_agent/diag/ds_am.log
   N/A                                     |N/A   |CloudTrail/DefaultLogGroup
   N/A                                     |N/A   |<accountId>-west-dev-vpc-flowlogs

1. Exit the root user

   ```ShellSession
   $ exit
   ```

1. Exit the API node

   ```ShellSession
   $ exit
   ```

1. Exit the controler

   ```ShellSession
   $ exit
   ```

1. Note that the following IAM policy has been created in Mgmt, Dev, Sbx, and Impl

   **Policy:** Ab2dCloudWatchLogsPolicy

   ```
   {
       "Version": "2012-10-17",
       "Statement": [
           {
               "Sid": "Stmt1584652230001",
               "Effect": "Allow",
               "Action": [
                   "logs:CreateLogGroup",
                   "logs:CreateLogStream",
                   "logs:PutLogEvents",
                   "logs:DescribeLogStreams"
               ],
               "Resource": [
                   "arn:aws:logs:*:*:*"
               ]
           }
       ]
   }
   ```

1. Note that the "Ab2dCloudWatchLogsPolicy" IAM policy to the "Ab2dInstanceRole" role in Mgmt, Dev, Sbx, and Impl

1. Connect to an API node in development

   1. Ensure that you are connected to the Cisco VPN

   1. Set the dev AWS profile

      ```ShellSession
      $ export AWS_PROFILE=ab2d-dev
      ```

   1. Connect to the development controller

      ```ShellSession
      $ ssh -i ~/.ssh/${AWS_PROFILE}.pem ec2-user@$(aws \
        --region us-east-1 ec2 describe-instances \
        --filters "Name=tag:Name,Values=ab2d-deployment-controller" \
        --query="Reservations[*].Instances[?State.Name == 'running'].PrivateIpAddress" \
        --output text)
      ```

   1. Connect to the desired API node

      *Example for connecting to the first API node:*

      ```ShellSession
      $ ssh ec2-user@$(./list-api-instances.sh \
        | grep 10. \
        | awk '{print $2}' \
        | head -n 1)
      ```

1. Download the CloudWatch Log agent

   1. Change to the "/tmp" directory

      ```ShellSession
      $ cd /tmp
      ```

   1. Download the CloudWatch Log Agent

      ```ShellSession
      $ curl -O https://s3.amazonaws.com/aws-cloudwatch/downloads/latest/awslogs-agent-setup.py
      ```

1. Configure the CloudWatch Log Agent and start logging "/var/log/messages"

   1. Enter the following

      ```ShellSession
      $ sudo python /tmp/awslogs-agent-setup.py --region us-east-1
      ```

   1. Note the following dependencies are downloaded

      ```
      AgentDependencies/
      AgentDependencies/awslogscli/
      AgentDependencies/awslogscli/urllib3-1.25.6.tar.gz
      AgentDependencies/awslogscli/jmespath-0.9.2.tar.gz
      AgentDependencies/awslogscli/colorama-0.3.7.zip
      AgentDependencies/awslogscli/idna-2.8.tar.gz
      AgentDependencies/awslogscli/awscli-1.11.41.tar.gz
      AgentDependencies/awslogscli/argparse-1.2.1.tar.gz
      AgentDependencies/awslogscli/botocore-1.13.9.tar.gz
      AgentDependencies/awslogscli/docutils-0.15.2.tar.gz
      AgentDependencies/awslogscli/pyasn1-0.2.3.tar.gz
      AgentDependencies/awslogscli/python-dateutil-2.6.0.tar.gz
      AgentDependencies/awslogscli/botocore-1.5.4.tar.gz
      AgentDependencies/awslogscli/jmespath-0.9.4.tar.gz
      AgentDependencies/awslogscli/awscli-cwlogs-1.4.6.tar.gz
      AgentDependencies/awslogscli/ordereddict-1.1.tar.gz
      AgentDependencies/awslogscli/futures-3.3.0.tar.gz
      AgentDependencies/awslogscli/futures-3.0.5.tar.gz
      AgentDependencies/awslogscli/certifi-2019.9.11.tar.gz
      AgentDependencies/awslogscli/six-1.12.0.tar.gz
      AgentDependencies/awslogscli/s3transfer-0.1.10.tar.gz
      AgentDependencies/awslogscli/six-1.10.0.tar.gz
      AgentDependencies/awslogscli/requests-2.18.4.tar.gz
      AgentDependencies/awslogscli/rsa-3.4.2.tar.gz
      AgentDependencies/awslogscli/s3transfer-0.2.1.tar.gz
      AgentDependencies/awslogscli/docutils-0.13.1.tar.gz
      AgentDependencies/awslogscli/urllib3-1.22.tar.gz
      AgentDependencies/awslogscli/awscli-1.16.273.tar.gz
      AgentDependencies/awslogscli/PyYAML-5.1.2.tar.gz
      AgentDependencies/awslogscli/idna-2.5.tar.gz
      AgentDependencies/awslogscli/PyYAML-3.12.tar.gz
      AgentDependencies/awslogscli/pyasn1-0.4.7.tar.gz
      AgentDependencies/awslogscli/colorama-0.4.1.tar.gz
      AgentDependencies/awslogscli/simplejson-3.3.0.tar.gz
      AgentDependencies/awslogscli/chardet-3.0.4.tar.gz
      AgentDependencies/virtualenv-15.1.0/
      AgentDependencies/virtualenv-15.1.0/setup.cfg
      AgentDependencies/virtualenv-15.1.0/tests/
      AgentDependencies/virtualenv-15.1.0/tests/__init__.py
      AgentDependencies/virtualenv-15.1.0/tests/test_cmdline.py
      AgentDependencies/virtualenv-15.1.0/tests/test_virtualenv.py
      AgentDependencies/virtualenv-15.1.0/tests/test_activate_output.expected
      AgentDependencies/virtualenv-15.1.0/tests/test_activate.sh
      AgentDependencies/virtualenv-15.1.0/scripts/
      AgentDependencies/virtualenv-15.1.0/scripts/virtualenv
      AgentDependencies/virtualenv-15.1.0/virtualenv.py
      AgentDependencies/virtualenv-15.1.0/MANIFEST.in
      AgentDependencies/virtualenv-15.1.0/README.rst
      AgentDependencies/virtualenv-15.1.0/AUTHORS.txt
      AgentDependencies/virtualenv-15.1.0/setup.py
      AgentDependencies/virtualenv-15.1.0/LICENSE.txt
      AgentDependencies/virtualenv-15.1.0/virtualenv_support/
      AgentDependencies/virtualenv-15.1.0/virtualenv_support/__init__.py
      AgentDependencies/virtualenv-15.1.0/virtualenv_support/wheel-0.29.0-py2.py3-none-any.whl
      AgentDependencies/virtualenv-15.1.0/virtualenv_support/argparse-1.4.0-py2.py3-none-any.whl
      AgentDependencies/virtualenv-15.1.0/virtualenv_support/pip-9.0.1-py2.py3-none-any.whl
      AgentDependencies/virtualenv-15.1.0/virtualenv_support/setuptools-28.8.0-py2.py3-none-any.whl
      AgentDependencies/virtualenv-15.1.0/PKG-INFO
      AgentDependencies/virtualenv-15.1.0/bin/
      AgentDependencies/virtualenv-15.1.0/bin/rebuild-script.py
      AgentDependencies/virtualenv-15.1.0/virtualenv.egg-info/
      AgentDependencies/virtualenv-15.1.0/virtualenv.egg-info/not-zip-safe
      AgentDependencies/virtualenv-15.1.0/virtualenv.egg-info/SOURCES.txt
      AgentDependencies/virtualenv-15.1.0/virtualenv.egg-info/entry_points.txt
      AgentDependencies/virtualenv-15.1.0/virtualenv.egg-info/top_level.txt
      AgentDependencies/virtualenv-15.1.0/virtualenv.egg-info/PKG-INFO
      AgentDependencies/virtualenv-15.1.0/virtualenv.egg-info/dependency_links.txt
      AgentDependencies/virtualenv-15.1.0/virtualenv_embedded/
      AgentDependencies/virtualenv-15.1.0/virtualenv_embedded/activate.ps1
      AgentDependencies/virtualenv-15.1.0/virtualenv_embedded/activate.csh
      AgentDependencies/virtualenv-15.1.0/virtualenv_embedded/deactivate.bat
      AgentDependencies/virtualenv-15.1.0/virtualenv_embedded/activate_this.py
      AgentDependencies/virtualenv-15.1.0/virtualenv_embedded/activate.fish
      AgentDependencies/virtualenv-15.1.0/virtualenv_embedded/python-config
      AgentDependencies/virtualenv-15.1.0/virtualenv_embedded/activate.bat
      AgentDependencies/virtualenv-15.1.0/virtualenv_embedded/distutils.cfg
      AgentDependencies/virtualenv-15.1.0/virtualenv_embedded/site.py
      AgentDependencies/virtualenv-15.1.0/virtualenv_embedded/activate.sh
      AgentDependencies/virtualenv-15.1.0/virtualenv_embedded/distutils-init.py
      AgentDependencies/virtualenv-15.1.0/docs/
      AgentDependencies/virtualenv-15.1.0/docs/userguide.rst
      AgentDependencies/virtualenv-15.1.0/docs/index.rst
      AgentDependencies/virtualenv-15.1.0/docs/development.rst
      AgentDependencies/virtualenv-15.1.0/docs/reference.rst
      AgentDependencies/virtualenv-15.1.0/docs/Makefile
      AgentDependencies/virtualenv-15.1.0/docs/conf.py
      AgentDependencies/virtualenv-15.1.0/docs/changes.rst
      AgentDependencies/virtualenv-15.1.0/docs/installation.rst
      AgentDependencies/virtualenv-15.1.0/docs/make.bat
      AgentDependencies/pip-6.1.1.tar.gz
      ```

   1. Wait for the following to display

      ```
      Step 1 of 5: Installing pip ...DONE
      ```

   1. Wait for the following to display

      *Note that this may take a while.*

      ```
      Step 2 of 5: Downloading the latest CloudWatch Logs agent bits ...DONE
      ```

   1. Note that the following is displayed

      ```
      Step 3 of 5: Configuring AWS CLI ...
      ```

   1. Press **return** on the keyboard to accept the default at the "AWS Access Key ID" prompt

   1. Press **return** on the keyboard to accept the default at the "AWS Secret Access Key" prompt

   1. Press **return** on the keyboard to accept the default at the "Default region name" prompt

   1. Press **return** on the keyboard to accept the default at the "Default output format" prompt

   1. Note that the following is displayed

      ```
      Step 4 of 5: Configuring the CloudWatch Logs Agent ...
      ```

   1. Add the following log by entering the following at the prompts

      - **Path of log file to upload:** /var/log/messages

      - **Destination Log Group name:** /aws/ec2/var/log/messages

      - **Choose Log Stream Name:** 1 *Use EC2 instance id*

      - **Choose Log Event timestamp format:** 3 *%Y-%m-%d %H:%M:%S (2008-09-08 11:52:54)*

      - **Choose initial position of upload:** 1 *From start of file.*

      - **More log files to configure:** N

   1. Wait for the following to display

      ```
      Step 5 of 5: Setting up agent as a daemon ...DONE
      ```

   1. Note the following output

      ------------------------------------------------------
      - Configuration file successfully saved at: /var/awslogs/etc/awslogs.conf
      - You can begin accessing new log events after a few moments at https://console.aws.amazon.com/cloudwatch/home?region=us-east-1#logs:
      - You can use 'sudo service awslogs start|stop|status|restart' to control the daemon.
      - To see diagnostic information for the CloudWatch Logs Agent, see /var/log/awslogs.log
      - You can rerun interactive setup using 'sudo python ./awslogs-agent-setup.py --region us-east-1 --only-generate-config'
      ------------------------------------------------------

1. View the "/var/log/messages" entry in the newly created CloudWatch Log agent

   1. Enter the following
   
      ```ShellSession
      $ sudo cat /var/awslogs/etc/awslogs.conf | tail -7
      ```

   1. Note the output

      ```
      [/var/log/messages]
      datetime_format = %Y-%m-%d %H:%M:%S
      file = /var/log/messages
      buffer_duration = 5000
      log_stream_name = {instance_id}
      initial_position = start_of_file
      log_group_name = /aws/ec2/var/log/messages
      ```

1. Ensure the CloudWatch Log Agent is running

   1. Check the status of the CloudWatch Log Agent

      ```ShellSession
      $ service awslogs status
      ```

   1. If the CloudWatch Log Agent is not running, start it by entering the following

      ```ShellSession
      $ sudo service awslogs start
      ```
      
1. Exit the API node

   ```ShellSession
   $ exit
   ```

1. Exit the controler

   ```ShellSession
   $ exit
   ```

### Verify logging to the \/aws\/ec2\/var\/log\/messages CloudWatch Log Group

1. Open Chrome

1. Log on to the development AWS account

1. Select **CloudWatch**

1. Select **Log groups** under the "Logs" section in the leftmost panel

1. Select the following

   ```
   /aws/ec2/var/log/messages
   ```

1. Select the instance id

   *Example:*

   ```
   i-01fbbfdf09d80d874
   ```

1. Note the list of events that appear within the main page

### Configure the \/aws\/ec2\/var\/log\/messages CloudWatch Log Group to Splunk HEC Lambda Configuration

> *** TO DO ***: Waiting for Splunk HEC URL AND Splunk HEC Token

1. Verify that the \/aws\/ec2\/var\/log\/messages CloudWatch Log Group has no subscription configured

   1. Open Chrome

   1. Log to to the development AWS account

   1. Select **CloudWatch**

   1. Select **Log groups** under the "Logs" section in the leftmost panel

   1. Select the radio button beside the following

      ```
      /aws/ec2/var/log/messages
      ```

   1. Select **Actions**

   1. If **Remove Subscription Filter** is enabled, select it and remove the subscription

1. Create a Lambda logger function for the \/aws\/ec2\/var\/log\/messages CloudWatch Log Group

   1. Open Chrome

   1. Log to to the development AWS account

   1. Select **Lambda**

   1. Select **Create function**

   1. Select **Use a blueprint**

   1. Enter the following in the **Filter by tags and attributes or search by keyword** text box

      ```
      splunk
      ```

   1. Select the following

      ```
      splunk-cloudwatch-logs-procesesor
      ```

   1. Select **Configure**

   1. Configure the "Basic Information" section as follows

      - **Function name:** clwg-logger-aws-ec2-var-log-messages

      - **Execution role radio button:** {selected}

      - **Execution role dropdown:** cwg_lambda_splunk_hec_role

   1. Configure the "CloudWatch Logs Trigger" section

      - **Log group:** /aws/ec2/var/log/messages

      - **Filter name:** clwg_logging_filter_aws_ec2_var_log_messages

      - **Enable trigger:** {checked}

      > *** TO DO ***: Waiting for Splunk HEC URL AND Splunk HEC Token


### Onboard additional CloudWatch log groups

> *** TO DO ***

## Appendix II: Get application load balancer access logs

1. Set AWS profile

   *Example for Sbx:*

   ```ShellSession
   $ export AWS_PROFILE=ab2d-sbx-sandbox
   ```

1. Set the region

   *Example for Sbx:*

   ```ShellSession
   $ export REGION=us-east-1
   ```

1. Output a string that provides datetime and ip address of person or entity accessing the load balancer

   *Example for Sbx:*

   ```ShellSession
   $ aws s3api list-objects --bucket ab2d-sbx-sandbox-cloudtrail --query "Contents[*].Key"
   ```
