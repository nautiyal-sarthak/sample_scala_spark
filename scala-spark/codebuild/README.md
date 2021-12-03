### my-datasci-cars-daps-ingestion CodeBuild Configuration files

---

#### This folder and it's sub-folders contain Kumo CodeBuild Projects configuration for my-datasci-cars-daps-ingestion and buildspec.  

* Master CodeBuild Project

| Folder | Filename | Details |
| --- | --- | --- |
| codebuild/my-datasci-cars-daps-ingestion-master/ | project.json | CodeBuild project for master branches, named "my-datasci-cars-daps-ingestion-master" |
| codebuild/my-datasci-cars-daps-ingestion-master/ | webhook.json | CodeBuild project webhook this project, project name must match |
| codebuild/my-datasci-cars-daps-ingestion-master/ | buildspec.yml | CodeBuild buildspec for this project, referred in project.json above |
| codebuild/my-datasci-cars-daps-ingestion-master/ | iam.json | Iam Role Definition for this CodeBuild project, project name is from project.json, suffix with -CODEBUILD |

* All CodeBuild Project

| Folder | Filename | Details |
| --- | --- | --- |
| codebuild/my-datasci-cars-daps-ingestion-all/ | project.json | CodeBuild project for non master branches, named  "my-datasci-cars-daps-ingestion-all" |
| codebuild/my-datasci-cars-daps-ingestion-all/ | webhook.json | CodeBuild project webhook this project, project name must match |
| codebuild/my-datasci-cars-daps-ingestion-all/ | buildspec.yml | CodeBuild buildspec for this project, referred in project.json above |
| codebuild/my-datasci-cars-daps-ingestion-all/ | iam.json | Iam Role Definition for this CodeBuild project, project name is from project.json, suffix with -CODEBUILD |


---

#### Kumo Notes:

##### VPC, ServiceRole, Cache Configurations are optional, the following defaults are used:
* Kumo creates CodeBuild Projects in segment's test environment application vpc and subnets in us-west-2.
* Kumo use pre-configured service role `arn:aws:iam::$accountId:role/KumoCodeBuildRole` if service role is not specified. Any Change made to this pre-configured service role will be overwritten.
  * You can provide a Kumo styled iam.json in the codebuild project directory to define an IAM Role to be used. Kumo will first create this role then use it for the codebuild project.
  * You can also use any IAM role created separately (such as the deployed application's role) and referring to it in project.json. In this case, Kumo will not use the pre-configured service role but try to use whatever you specified.
* Kumo setup cache in s3://\<segment\>-test-\<accountId\>-build-artifacts/my-datasci-cars-daps-ingestion/cache

##### A secondary repository source is automatically added to CodeBuild Project by Kumo. It contains utility scripts to upload build artifact to storage where Kumo can use for deployment etc. However if you define an empty secondarySources in your project.json, this repository will not be added.
* https://github.expedia.biz/Kumo/kumo-codebuild-scripts
  ```
  "secondarySources": [
    {
      "type": "GITHUB_ENTERPRISE",
      "location": "https://github.expedia.biz/Kumo/kumo-codebuild-scripts.git",
      "sourceIdentifier": "kumo_codebuild_scripts"
    }
  ],
  ```

---

Further Information: https://confluence.expedia.biz/display/EWECare/CodeBuild+FAQ
