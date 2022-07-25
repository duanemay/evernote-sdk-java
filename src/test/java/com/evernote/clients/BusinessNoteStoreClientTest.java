/*
 * Copyright 2013 Evernote Corporation
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.evernote.clients;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.SharedNotebook;
import com.evernote.edam.type.User;
import com.evernote.edam.userstore.AuthenticationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BusinessNoteStoreClientTest {

    // If set, test with actual API calls
    String token = null;

    BusinessNoteStoreClient client;

    @BeforeEach
    void initialize() throws Exception {
        if (token == null) {
            NoteStoreClient noteStoreClient = mock(NoteStoreClient.class);

            SharedNotebook sharedNotebook = new SharedNotebook();

            Notebook createdNotebook = new Notebook();
            createdNotebook.setSharedNotebooks(List.of(sharedNotebook));
            when(noteStoreClient.createNotebook(any(Notebook.class))).thenReturn(
                    createdNotebook);

            NoteStoreClient personalClient = mock(NoteStoreClient.class);

            List<LinkedNotebook> listLinkedNotebooks = new ArrayList<>();
            when(personalClient.listLinkedNotebooks()).thenReturn(listLinkedNotebooks);

            LinkedNotebook createdLinkedNotebook = new LinkedNotebook();
            when(personalClient.createLinkedNotebook(isA(LinkedNotebook.class)))
                    .thenReturn(createdLinkedNotebook);

            User user = new User();
            user.setUsername("username");
            user.setShardId("shardId");
            AuthenticationResult authenticationResult = new AuthenticationResult();
            authenticationResult.setUser(user);

            client = new BusinessNoteStoreClient(personalClient, noteStoreClient,
                    authenticationResult);
        } else {
            EvernoteAuth auth = new EvernoteAuth(EvernoteService.SANDBOX, token);
            client = new ClientFactory(auth).createBusinessNoteStoreClient();
        }
    }

    @Test
    void testCreateNotebook() throws Exception {
        Notebook notebook = new Notebook();
        notebook.setName("BusinessNoteStoreClientTest#testCreateNotebook");
        LinkedNotebook createdLinkedNotebook = client.createNotebook(notebook);
        assertNotNull(createdLinkedNotebook);
    }

    @Test
    void testListNotebooks() throws Exception {
        List<LinkedNotebook> listLinkedNotebooks = client.listNotebooks();
        for (LinkedNotebook linkedNotebook : listLinkedNotebooks) {
            assertTrue(linkedNotebook.isSetBusinessId());
        }
    }

}
