/*
 * (C) Copyright 2020 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Salem Aouana
 */

package org.nuxeo.ecm.core.blob;

import static org.junit.Assert.assertNotNull;

import java.io.Serializable;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.mockito.MockitoFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

/**
 * @since 11.1
 */
/*
 * @RunWith(FeaturesRunner.class)
 * @Features({ DocumentBlobColdStorageFeature.class, MockitoFeature.class })
 * @Deploy("org.nuxeo.ecm.core.api.tests:OSGI-INF/test-blob-provider-inmemory.xml")
 */

@RunWith(FeaturesRunner.class)
@Features({ BlobManagerFeature.class, DocumentBlobManagerFeature.class, MockitoFeature.class })
@Deploy("org.nuxeo.ecm.core.api.tests:OSGI-INF/dummy-blob-provider.xml")
public class TestDocumentBlobColdStorage {

    @Inject
    protected CoreSession session;

    @Inject
    protected TransactionalFeature transactionalFeature;

    @Test
    public void should() {
        DocumentModel doc = session.createDocumentModel("/", "AnyFile", "File");
        doc = session.createDocument(doc);
        Blob blob = Blobs.createBlob("foo");
        doc.setPropertyValue("file:content", (Serializable) blob);
        transactionalFeature.nextTransaction();

        doc = session.getDocument(doc.getRef());
        assertNotNull(doc.getPropertyValue("file:content"));
    }

}
