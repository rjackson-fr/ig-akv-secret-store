# Azure Key Vault Secret Store

A secret store plugin for IG (tested on 2023.11.0) to retrieve secrets from Azure Key Vault at run-time.

## Prerequisites

The AKV IG secret store plugin requires an implementation of the commons-secrets backend for AKV (dowload from releases)

## Notes on AKV secrets

AKV hosts 3 kinds of secrets:
* Keys - not exportable.  Only used for API-based cryptographic functions
* Secrets - Used for passwords or HMAC keys (ie Generic Secrets)
* Certificates - Used for keys and/or certificates (SSL, Signing, Verification, etc)

The backend plugin uses Azure Java SDK: https://azure.github.io/azure-sdk-for-java/keyvault.html

The plugin can use the "DefaultAzureCredential" class for authenticated which is a "passwordless" authentication method.
https://learn.microsoft.com/en-us/java/api/com.azure.identity.defaultazurecredential?view=azure-java-stable.
This will inherit the login session from the environment, vm service account, Azure CLI, etc

If provided credentials, it can also use the "ClientSecretCredential" class for authentication:
https://learn.microsoft.com/en-us/java/api/com.azure.identity.clientsecretcredential?view=azure-java-stable
Currenltly these credentials will be stored in AM's configuration (ecrypted with AM encryption).  In future, the client secret could be referenced from another secret store using a secret label (similar to Google Secret Manager plugin)


## Configuring AKV Secret Store in IG

The first step is to install this plugin into IG. The plugin can be downloaded from the current releases here. Copy the jar file into the ~/.openig/config/extra (or ./config/extra directory within your IG project) 

The plugin can be used in place of other secret stores in cconfig json files:

## Example 1 - using default credentials (60 seconds validity on fetched secrets)
```json{
    "name": "AKVSecretStore-1",
    "type": "org.forgerock.openig.secrets.AzureKeyVaultSecretStoreHeaplet",
    "config": {
        "keyVaultName": "ciam-dev",
        "expirySeconds": 60
    }
},
```

## Example 2 - using client secret credentials (600 seconds validity on fetched secrets)
```json{
    "name": "AKVSecretStore-1",
    "type": "org.forgerock.openig.secrets.AzureKeyVaultSecretStoreHeaplet",
    "config": {
        "keyVaultName": "ciam-dev",
        "azureClientId": "8fe98d7d-0901-41f0-8dca-97f463d8924e",
        "azureClientSecret": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
        "azureTenantId": "727db4fd-b9f7-XXXX-9d09-25364ba0ee68",
        "expirySeconds": 600
    }
},
```


