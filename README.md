# Nordic Smart Government Reference Implementation
As part of the project [Nordic Smart Government](https://nordicsmartgovernment.org/), this reference implementation has been made to demonstrate and define the proposed standard APIs for adding and retrieving information about business transactions in a business systems.

For instance, by POST-ing a Peppol BIS 3 invoice, a transaction is created based on the invoice, and the details about the transaction can then be retreived as XBRL GL.

## Important Disclaimer
**Be aware** that there are **NO GUARANTEES** for stability and availability of the Reference Implementation or it's data! Please [let us know](mailto:steinar.skagemo@brreg.no) in advance if you are planning to do some extensive testing of the reference implementation in a specific period, so that we can try to avoid re-setting the data while you are testing.

## Exploring the API
Swagger UI: https://nsg.fellesdatakatalog.brreg.no/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#

Example Curl-request that will return a list of transactionIds for the company with companyId 20202020
```bash
curl -X GET "https://nsg.fellesdatakatalog.brreg.no/transactions/20202020?invoiceType=all" -H "accept: application/json"
```

Example Curl-request that will return details about a specific transaction in XBRL GL:

```bash
curl -X GET "https://nsg.fellesdatakatalog.brreg.no/transactions/20202020/0164ee71-1334-4e7e-9002-8830db6d61ab" -H "accept: application/xbrl-instance+xml"
```

## Test data
### Provided testdata
The Reference Implementation comes with some test data that will be reset whenever the reference-implementation is re-deployed (part of the [code](https://github.com/nordicsmartgovernment/nordicsmartgovernment/tree/develop/src/main/resources/SyntheticData)).

These data are purchase and sales invoices and bank statements for a company with companyId 20202020 (used in the examples above), and purchase and sales invoices for a company with companyId 1252525-9. [More details](https://docs.google.com/document/d/12a1i9_e4s-zC_JH-KQeuvCy9taYPO0aLUDR6DEzobQM/edit#heading=h.gizf2iiupa45)

### Bring your own test-data
By POST-ing invoices and other supported business transaction documents, you will add data to the reference implementation that can later be accessed either as the original documents (see [document-API](https://nsg.fellesdatakatalog.brreg.no/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/document-api) or as standardised transaction information (see [transactions-API](https://nsg.fellesdatakatalog.brreg.no/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/transactions-api). 

## More detailed documentation
The NSG-project has created a [more detailed introduction](https://docs.google.com/document/d/12a1i9_e4s-zC_JH-KQeuvCy9taYPO0aLUDR6DEzobQM/edit#) to the purpose and the functionality of the NSG Reference Implementation.


## Feedback
All feedback is welcome, whether it is related to technical issues, ideas for improvements of the APIs or questions about the available documentation. Please give feedback as [issues](https://github.com/nordicsmartgovernment/nordicsmartgovernment/issues) or by [e-mail](steinar.skagemo@brreg.no).


# Running your own instance of the NSG Reference Implementation

## Update Linux
```
sudo apt update
sudo apt-get update
sudo apt-get upgrade
```

## Install java, git, maven, docker og docker-compose

##### For Linux
```
sudo apt-get install default-jdk git maven docker.io docker-compose
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
sudo adduser ${USER} docker
sudo adduser ${USER} mvn
```

Check that they have been added with "id -nG", force the update with a reboot or with "su - ${USER}"

## Environment variables
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
Open ~/.bashrc and add the lines
```
export NSG_POSTGRES_DB="nsg_db"
export NSG_POSTGRES_DBO_PASSWORD="Passw0rd"
export NSG_POSTGRES_DBO_USER="nsg_dbo"
export NSG_POSTGRES_HOST="postgres:5432"
export NSG_POSTGRES_PASSWORD="Passw0rd"
export NSG_POSTGRES_USER="nsg"
```
Update from ~/.bashrc with
```
source ~/.bashrc
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
