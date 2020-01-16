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

import static org.nuxeo.ecm.core.schema.FacetNames.COLD_STORAGE;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.thumbnail.ThumbnailService;
import org.nuxeo.runtime.api.Framework;

/**
 * Manages the cold storage of the {@link Blob} associated to a {@link DocumentModel}.
 *
 * @since 11.1
 */
public class DocumentBlobColdStorageHelper {

    private static final Logger log = LogManager.getLogger(DocumentBlobColdStorageHelper.class);

    public static final String FILE_CONTENT_PROPERTY = "file:content";

    public static final String COLD_STORAGE_CONTENT_PROPERTY = "coldstorage:content";

    /**
     * Moves the blob associated with the given {@link DocumentModel} into a cold storage.
     *
     * @param session the core session
     * @param documentRef the reference of the document associated with the blob and that we want to move to cold
     *            storage
     */
    public static void moveBlob(CoreSession session, DocumentRef documentRef) {
        DocumentModel document = session.getDocument(documentRef);
        if (document.hasFacet(COLD_STORAGE)) {
            log.trace("The blob content of document: {} is already on cold storage", document);
            return;
        }
        document.addFacet(COLD_STORAGE);
        Serializable currentContent = document.getPropertyValue(FILE_CONTENT_PROPERTY);
        if (currentContent != null) {
            document.setPropertyValue(COLD_STORAGE_CONTENT_PROPERTY, currentContent);
            Blob thumbnail = Framework.getService(ThumbnailService.class).getThumbnail(document, session);
            document.setPropertyValue(FILE_CONTENT_PROPERTY, (Serializable) thumbnail);
            session.saveDocument(document);
            session.save();
        }
    }

    private DocumentBlobColdStorageHelper() {
        // no instance allowed
    }
}
