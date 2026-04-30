# Jenkins setup

Before running the [Jenkinsfile](carcassonne/Jenkinsfile), you need to follow the steps below:

## 1. Set up SonarQube
As described in the [SonarQube instructions](./sonarqube-instructions.md)

## 2. Create DockerHub Credentials
1. In DockerHub, create an access token by going to your account settings > Security > New Access Token. Give it a name and select the appropriate permissions (e.g., read/write).
2. Copy the generated token and keep it safe, as you won't be able to see it again.
3. Go to Jenkins dashboard.
4. Click on "Manage Jenkins" > "Manage Credentials".
5. Click on "Global" > "Add Credentials".
6. Select "Username with password" as the kind.
7. Enter your DockerHub username in the "Username" field and paste the access token you copied earlier into the "Password" field.
8. Give it the ID 'MyDockerAccount' and a description, then click "OK".

## 3. Create DockerHub repo variable
1. Go to Jenkins dashboard.
2. Click on "Manage Jenkins" > "Manage Credentials".
3. Click on "Global" > "Add Credentials".
4. Select "Secret text" as the kind.
5. Enter your DockerHub repository name (e.g., 'juanvalenzuela101/carcassonne_v1_2026') in the "Secret" field.
6. Give it the ID 'carcassonneDockerHubRepo' and a description, then click "OK".

# DockerHub repo variable ID: *carcassonneDockerHubRepo*

# DockerHub credentials ID: *MyDockerAccount*

