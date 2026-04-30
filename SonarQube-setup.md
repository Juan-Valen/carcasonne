# SonarQube Setup Guide

This guide will walk you through the steps to set up SonarQube for our project.

## 1. Install SonarQube plugin for Jenkins
1. Go to Jenkins dashboard.
2. Click on "Manage Jenkins" > "Manage Plugins".
3. In the "Available" tab, search for "SonarQube Scanner for Jenkins".
4. Select the plugin and install it.

## 2. Get SonarQube token
1. Log in to your SonarQube instance.
2. Click on your profile picture in the top right corner and select "My Account".
3. Go to the "Security" tab.
4. Under "Tokens", enter a name for your token (e.g., "Jenkins Token") and click "Generate".
5. Copy the generated token and keep it safe, as you won't be able to see it again.

## 3. Add SonarQube token to Jenkins credentials
1. Go back to Jenkins dashboard.
2. Click on "Manage Jenkins" > "Manage Credentials".
3. Click on "Global" > "Add Credentials".
4. Select "Secret text" as the kind.
5. Paste the SonarQube token you copied earlier into the "Secret" field.
6. Give it an ID (e.g., "sonar-token") and a description, then click "OK".

## 4. Configure SonarQube in Jenkins
1. Go back to Jenkins dashboard.
2. Click on "Manage Jenkins" > "Configure System".
3. Scroll down to the "SonarQube servers" section and click "Add SonarQube".
4. Enter the name: 'carcassonneSonarQube'
5. Enter the URL of your SonarQube instance (e.g., http://localhost:9000).
6. Under "Server authentication token", select "Credentials" and choose the token you added in the previous step (e.g., "sonar-token").
7. Click "Save" to apply the changes.

# SonarQube server name: *carcassonneSonarQube*