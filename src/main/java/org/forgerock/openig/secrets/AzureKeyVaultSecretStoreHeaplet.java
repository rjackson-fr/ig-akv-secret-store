/*
 * Copyright 2018-2023 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package org.forgerock.openig.secrets;

import static java.lang.String.format;
import static org.forgerock.json.JsonValueFunctions.file;
import static org.forgerock.openig.heap.Keys.SCHEDULED_EXECUTOR_SERVICE_HEAP_KEY;
import static org.forgerock.openig.util.JsonValues.evaluated;
import static org.forgerock.openig.util.JsonValues.requiredHeapObject;
//import static org.forgerock.secrets.akv.AzureKeyVaultSecretStore.AzureKeyVaultSecretStoreBuilder;

import org.forgerock.openig.heap.Name;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

import org.forgerock.config.resolvers.FlatFileResolver;
import org.forgerock.json.JsonValue;
import org.forgerock.openig.heap.GenericHeaplet;
import org.forgerock.openig.heap.Heap;
import org.forgerock.openig.heap.HeapException;
import org.forgerock.openig.model.type.service.TypeInfo;
import org.forgerock.secrets.GenericSecret;
import org.forgerock.secrets.Secret;
import org.forgerock.secrets.SecretStore;
import org.forgerock.secrets.akv.AzureKeyVaultSecretStore;
//import org.forgerock.secrets.akv.AzureKeyVaultSecretStore.AzureKeyVaultSecretStoreBuilder;
import org.forgerock.secrets.propertyresolver.PropertyResolverSecretStore;
import org.forgerock.secrets.propertyresolver.SecretPropertyFormat;
import org.forgerock.util.DirectoryWatcher;
import org.forgerock.util.annotations.VisibleForTesting;
import org.slf4j.LoggerFactory;
import org.forgerock.util.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This heaplet represents an instance of a {@link AzureKeyVaultSecretStore} resolving properties from AKV
 * <pre>
 * {@code {
 *       "type": "AzureKeyVaultSecretStore",
 *       "config": {
 *         "keyVaultName":            string             [REQUIRED - name of the AKV Secret Store]
 *         "expirySeconds":           integer            [OPTIONAL - timeout in seconds for secret validity]
 *    }
 * }
 * }</pre>
 * <p>
 * Example:
 * <pre>
 * {@code {
 *       "type": "AzureKeyVaultSecretStore",
 *       "config": {
 *         "keyVaultName": "myvault"
 *         "expirySeconds": 600
 *       }
 *    }
 * }</pre>
 */

//@TypeInfo(AzureKeyVaultSecretStoreTypeProvider.class)
public class AzureKeyVaultSecretStoreHeaplet extends GenericHeaplet {

    static final String CONFIG_KEY_VAULT_NAME = "keyVaultName";
    static final String CONFIG_KEY_EXPIRY_SECONDS = "expirySeconds";
    static final String CONFIG_KEY_CLIENT_ID = "azureClientId";
    static final String CONFIG_KEY_CLIENT_SECRET = "azureClientId";
    static final String CONFIG_KEY_TENANT_ID = "azureTenantId";
    

    private static final Logger logger = LoggerFactory.getLogger(AzureKeyVaultSecretStoreHeaplet.class);


    @Override
    public AzureKeyVaultSecretStore create() throws HeapException {
        //super.create(name, config, heap);
        
        JsonValue evaluated = config.as(evaluatedWithHeapProperties());
        try {
            final String keyVaultName = evaluated.get(CONFIG_KEY_VAULT_NAME)
                                        .as(evaluatedWithHeapProperties())
                                        //.required()
                                        .defaultTo("robakv")
                                        .expect(String.class)
                                        .asString();
            logger.warn("AzureKeyVaultSecretStore::create keyVaultName:" + keyVaultName);
            final Integer expirySeconds = evaluated.get(CONFIG_KEY_EXPIRY_SECONDS)
                                        .as(evaluatedWithHeapProperties())
                                        .defaultTo(600)
                                        .asInteger();
            logger.warn("AzureKeyVaultSecretStore::create expirySeconds:" + expirySeconds);

            final String clientId = evaluated.get(CONFIG_KEY_CLIENT_ID)
                                        .as(evaluatedWithHeapProperties())
                                        //.required()
                                        //.defaultTo("robakv")
                                        .expect(String.class)
                                        .asString();
            final String clientSecret = evaluated.get(CONFIG_KEY_CLIENT_SECRET)
                                        .as(evaluatedWithHeapProperties())
                                        //.required()
                                        //.defaultTo("robakv")
                                        .expect(String.class)
                                        .asString();
            final String tenantId = evaluated.get(CONFIG_KEY_TENANT_ID)
                                        .as(evaluatedWithHeapProperties())
                                        //.required()
                                        //.defaultTo("robakv")
                                        .expect(String.class)
                                        .asString();
            if (clientSecret != null) {
                return new AzureKeyVaultSecretStore(keyVaultName, clientId, clientSecret, tenantId, expirySeconds);
            }

            return new AzureKeyVaultSecretStore(keyVaultName, expirySeconds);
        }
        finally {
            // Reset secret values in config (at next gc)
            config.put(CONFIG_KEY_VAULT_NAME, null);
    }

    }



    @Override
    public void destroy() {
        super.destroy();
    }

/* 
    @Override
    public Object create() throws HeapException {
        // TODO Auto-generated method stub
        return null;
    }*/
}
