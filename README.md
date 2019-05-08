# nordicsmartgovernment
A reference implementation of managed business transactions

# Get started

## Update Linux
```
sudo apt update
sudo apt-get update
sudo apt-get upgrade
```

## Install java, git, maven, docker og docker-compose

##### For Linux
```
sudo apt-get install default-jdk
sudo apt install git
sudo apt install maven
sudo apt install docker.io
sudo apt install docker-compose
```

##### For Windows
Git for Windows - https://gitforwindows.org/

Apache Maven - http://maven.apache.org/download.cgi

Docker for Windows - https://hub.docker.com/editions/community/docker-ce-desktop-windows

## Steps only be necessary for Linux

##### Configure Docker to start on Boot
```
systemctl start docker
systemctl enable docker
```

##### Enable executing Docker and Maven without sudo
```
sudo usermod -aG docker ${USER}
sudo usermod -aG mvn ${USER}
su - ${USER}
```

Check that they have been added with "id -nG"

## Enviorment variables
These are needed to connect to the local database
```
NSG_POSTGRES_DB="nsg_db"
NSG_POSTGRES_DBO_PASSWORD="Passw0rd"
NSG_POSTGRES_DBO_USER="nsg_dbo"
NSG_POSTGRES_HOST="postgres:5432"
NSG_POSTGRES_PASSWORD="Passw0rd"
NSG_POSTGRES_USER="nsg"
```

##### For linux
```
sudo -H gedit /etc/environment
source /etc/environment
```

Check that they have been added with "printenv"

##### For windows
“Advanced system settings” → “Environment Variables”

## Nice to have
#### Postman
https://www.getpostman.com/

#### pgAdmin
https://www.pgadmin.org/

## Clone and run
```
git clone {repo}
mvn clean install
docker-compose up -d
```
-d enables "detached mode"

## Test that everything is running
"/src/main/resources/openAPI/examples/NordicSmartGovernment.postman_collection.json"

Import this collection in Postman and test the app locally.
