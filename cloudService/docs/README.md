# Documentation de la partie serveur

## Configuration d'une instance

### Dropbox

Créez un fichier dropbox.config à la racine du projet. Il doit contenir :

```json
{
    "app_key": "KEY",
    "app_secret": "SECRET",
    "callback_url": "http://localhost:8080/kiwishare/callbackDropbox"
}
```

N'oubliez pas de configurer votre application pour qu'elle accepte cette adresse de callback.

### Drive

Créez un fichier drive.config à la racine du projet. Il doit contenir :

```json
{
    "app_key": "KEY",
    "app_secret": "SECRET",
    "callback_url": "http://localhost:8080/kiwishare/callbackDrive"
}
```

N'oubliez pas de configurer votre application pour qu'elle accepte cette adresse de callback.

### GPG

Le chiffrement des fichiers est réalisé à l'aide de GnuPG. Vous devez donc l'installer sur votre machine. De plus pour configurer le chiffrement, il va falloir générer une clé.

```bash
gpg --gen-key
```

Une fois votre clé créée, il faut rajouter ces 3 lignes au fichier _~/.gnupg/gpg.conf_ :

```bash
default-key IDDELACLE
default-recipient some-user-id
default-recipient-self
```

Vous pouvez ensuite créer le fichier de config comme il suit :

```json
{
  "gpg_key":"ID",
  "gpg_pass":"PASSPHRASE"
}
```

## Lancement d'une instance

Voici les commandes pour lancer le serveur :

```bash
mvn compile
mvn jetty:run
```

## URIs disponibles

### /authurl

#### Description:

Obtention des URIs d'authentification

#### URI:

`http://localhost:8080/kiwishare/authurl`

#### Methode:

**GET**

#### Retour:

La liste des urls d'authentification des différents service. Exemple :

```json
{
    "urls": [{
        "service": "dropbox",
        "url": "https://www.dropbox.com/1/oauth2/authorize?response_type=code&client_id=n3ukjy5rxfu5dnc&redirect_uri=http://localhost:8080/kiwishare/callbackDropbox"
    }, {
        "service": "drive",
        "url": "https://accounts.google.com/o/oauth2/v2/auth?response_type=code&scope=https://www.googleapis.com/auth/drive&client_id=462659653340-ckldp4re47tg7slfj8q3tsvc6ur59657.apps.googleusercontent.com&redirect_uri=http://localhost:8080/kiwishare/callbackDrive"
    }]
}
```

### /get

#### Description:

Récupère les informations sur un fichier.

#### URI:

`http://localhost:8080/kiwishare/get`

#### Paramètres

**path** = Le chemin du fichier à récupérer.<br>
_Exemple_ : `http://localhost:8080/kiwishare/get?path=generate/test.jpg`

#### Methode:

**GET**

#### Retour:

Des informations sur le fichier. Dans l'exemple suivant, le fichier n'existe que du côté google drive :

```json
{
    "dropbox": {
        "err": "Unable to parse json "
    },
    "drive": {
        "lastModifyingUserName": "Projet SI",
        "shared": false,
        "downloadUrl": "https://doc-0k-6o-docs.googleusercontent.com/docs/securesc/0ihp9lv9fmpj0uolejr0nncm7av0o7fi/jnsabk2mqqks7slm44a0j8u4nkukcgqe/1463328000000/09758181586404959543/09758181586404959543/0B_i6_IkJyufESmlCcVBRSlJaYVU?e=download&gd=true",
        "owners": [{
            "permissionId": "09758181586404959543",
            "isAuthenticatedUser": true,
            "emailAddress": "projetsi.drive@gmail.com",
            "kind": "drive#user",
            "displayName": "Projet SI"
        }],
        "mimeType": "image/jpeg",
        "title": "test.jpg",
        "thumbnailLink": "https://lh3.googleusercontent.com/bR0OQfyXFhEWuOZv_s97S5b-CxcKGyMtUwhIPnkIhEl8v35-Vq56-vhNK1JIF9pCxeaNGQ=s220",
        "quotaBytesUsed": "49868",
        "md5Checksum": "756427090fa89dd475a7d2f350dc2e10",
        "lastModifyingUser": {
            "permissionId": "09758181586404959543",
            "isAuthenticatedUser": true,
            "emailAddress": "projetsi.drive@gmail.com",
            "kind": "drive#user",
            "displayName": "Projet SI"
        },
        "copyable": true,
        "iconLink": "https://ssl.gstatic.com/docs/doclist/images/icon_11_image_list.png",
        "fileExtension": "jpg",
        "alternateLink": "https://drive.google.com/file/d/0B_i6_IkJyufESmlCcVBRSlJaYVU/view?usp=drivesdk",
        "id": "0B_i6_IkJyufESmlCcVBRSlJaYVU",
        "modifiedByMeDate": "2016-05-14T10:32:05.405Z",
        "lastViewedByMeDate": "2016-05-14T10:32:05.405Z",
        "webContentLink": "https://docs.google.com/uc?id=0B_i6_IkJyufESmlCcVBRSlJaYVU&export=download",
        "writersCanShare": true,
        "userPermission": {
            "role": "owner",
            "kind": "drive#permission",
            "etag": "\"An1NegRH_Q1WfUh5qt1ofDI9qPI/pjiTqQXB9-ML12InSQQTCp_qx54\"",
            "id": "me",
            "type": "user",
            "selfLink": "https://www.googleapis.com/drive/v2/files/0B_i6_IkJyufESmlCcVBRSlJaYVU/permissions/me"
        },
        "kind": "drive#file",
        "editable": true,
        "ownerNames": ["Projet SI"],
        "version": "17",
        "selfLink": "https://www.googleapis.com/drive/v2/files/0B_i6_IkJyufESmlCcVBRSlJaYVU",
        "labels": {
            "starred": false,
            "hidden": false,
            "restricted": false,
            "viewed": true,
            "trashed": false
        },
        "markedViewedByMeDate": "1970-01-01T00:00:00.000Z",
        "appDataContents": false,
        "explicitlyTrashed": false,
        "createdDate": "2016-05-14T10:32:05.405Z",
        "fileSize": "49868",
        "modifiedDate": "2016-05-14T10:32:05.405Z",
        "imageMediaMetadata": {
            "rotation": 0,
            "width": 425,
            "height": 593
        },
        "spaces": ["drive"],
        "etag": "\"An1NegRH_Q1WfUh5qt1ofDI9qPI/MTQ2MzIyMTkyNTQwNQ\"",
        "originalFilename": "test.jpg",
        "parents": [{
            "parentLink": "https://www.googleapis.com/drive/v2/files/0B_i6_IkJyufEcVdwQjJNd1VNa28",
            "isRoot": false,
            "kind": "drive#parentReference",
            "id": "0B_i6_IkJyufEcVdwQjJNd1VNa28",
            "selfLink": "https://www.googleapis.com/drive/v2/files/0B_i6_IkJyufESmlCcVBRSlJaYVU/parents/0B_i6_IkJyufEcVdwQjJNd1VNa28"
        }],
        "headRevisionId": "0B_i6_IkJyufEcEkxZTJOR2VDTzBlbFJFcCtUQzZVdVdTckJrPQ"
    }
}
```

### /put

**TODO**

### /info

#### Description:

Récupère les informations sur les services.

#### URI:

`http://localhost:8080/kiwishare/info`

#### Methode:

**GET**

#### Retour:

Exemple :

```json
{
    "dropbox": {
        "err": "Unable to parse json null"
    },
    "drive": {
        "permissionId": "09758181586404959543",
        "largestChangeId": "151",
        "kind": "drive#about",
        "quotaBytesUsedAggregate": "167764",
        "folderColorPalette": ["#ac725e", "#d06b64", "#f83a22", "#fa573c", "#ff7537", "#ffad46", "#fad165", "#fbe983", "#b3dc6c", "#7bd148", "#16a765", "#42d692", "#92e1c0", "#9fe1e7", "#9fc6e7", "#4986e7", "#9a9cff", "#b99aff", "#a47ae2", "#cd74e6", "#f691b2", "#cca6ac", "#cabdbf", "#8f8f8f"],
        "rootFolderId": "0APi6_IkJyufEUk9PVA",
        "isCurrentAppInstalled": false,
        "quotaBytesByService": [{
            "bytesUsed": "0",
            "serviceName": "DRIVE"
        }, {
            "bytesUsed": "0",
            "serviceName": "GMAIL"
        }, {
            "bytesUsed": "0",
            "serviceName": "PHOTOS"
        }],
        "additionalRoleInfo": [{
            "type": "application/vnd.google-apps.drawing",
            "roleSets": [{
                "primaryRole": "reader",
                "additionalRoles": ["commenter"]
            }]
        }, {
            "type": "application/vnd.google-apps.document",
            "roleSets": [{
                "primaryRole": "reader",
                "additionalRoles": ["commenter"]
            }]
        }, {
            "type": "application/vnd.google-apps.presentation",
            "roleSets": [{
                "primaryRole": "reader",
                "additionalRoles": ["commenter"]
            }]
        }, {
            "type": "*",
            "roleSets": [{
                "primaryRole": "reader",
                "additionalRoles": ["commenter"]
            }]
        }, {
            "type": "application/vnd.google-apps.spreadsheet",
            "roleSets": [{
                "primaryRole": "reader",
                "additionalRoles": ["commenter"]
            }]
        }, {
            "type": "application/vnd.google-apps.*",
            "roleSets": []
        }],
        "languageCode": "fr",
        "selfLink": "https://www.googleapis.com/drive/v2/about",
        "quotaBytesUsed": "167764",
        "quotaType": "LIMITED",
        "features": [{
            "featureName": "ocr"
        }, {
            "featureRate": 2,
            "featureName": "translation"
        }],
        "quotaBytesTotal": "16106127360",
        "quotaBytesUsedInTrash": "0",
        "domainSharingPolicy": "allowed",
        "importFormats": [{
            "source": "application/x-vnd.oasis.opendocument.presentation",
            "targets": ["application/vnd.google-apps.presentation"]
        }, {
            "source": "text/tab-separated-values",
            "targets": ["application/vnd.google-apps.spreadsheet"]
        }, {
            "source": "image/jpeg",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "image/bmp",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "image/gif",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "application/vnd.ms-excel.sheet.macroenabled.12",
            "targets": ["application/vnd.google-apps.spreadsheet"]
        }, {
            "source": "application/vnd.openxmlformats-officedocument.wordprocessingml.template",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "application/vnd.ms-powerpoint.presentation.macroenabled.12",
            "targets": ["application/vnd.google-apps.presentation"]
        }, {
            "source": "application/vnd.ms-word.template.macroenabled.12",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "image/pjpeg",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "application/vnd.google-apps.script+text/plain",
            "targets": ["application/vnd.google-apps.script"]
        }, {
            "source": "application/vnd.ms-excel",
            "targets": ["application/vnd.google-apps.spreadsheet"]
        }, {
            "source": "application/vnd.sun.xml.writer",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "application/vnd.ms-word.document.macroenabled.12",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "application/vnd.ms-powerpoint.slideshow.macroenabled.12",
            "targets": ["application/vnd.google-apps.presentation"]
        }, {
            "source": "text/rtf",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "text/plain",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "application/vnd.oasis.opendocument.spreadsheet",
            "targets": ["application/vnd.google-apps.spreadsheet"]
        }, {
            "source": "application/x-vnd.oasis.opendocument.spreadsheet",
            "targets": ["application/vnd.google-apps.spreadsheet"]
        }, {
            "source": "image/png",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "application/x-vnd.oasis.opendocument.text",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "application/msword",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "application/pdf",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "application/json",
            "targets": ["application/vnd.google-apps.script"]
        }, {
            "source": "application/x-msmetafile",
            "targets": ["application/vnd.google-apps.drawing"]
        }, {
            "source": "application/vnd.openxmlformats-officedocument.spreadsheetml.template",
            "targets": ["application/vnd.google-apps.spreadsheet"]
        }, {
            "source": "application/vnd.ms-powerpoint",
            "targets": ["application/vnd.google-apps.presentation"]
        }, {
            "source": "application/vnd.ms-excel.template.macroenabled.12",
            "targets": ["application/vnd.google-apps.spreadsheet"]
        }, {
            "source": "image/x-bmp",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "application/rtf",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "application/vnd.openxmlformats-officedocument.presentationml.template",
            "targets": ["application/vnd.google-apps.presentation"]
        }, {
            "source": "image/x-png",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "text/html",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "application/vnd.oasis.opendocument.text",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "targets": ["application/vnd.google-apps.presentation"]
        }, {
            "source": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "targets": ["application/vnd.google-apps.spreadsheet"]
        }, {
            "source": "application/vnd.google-apps.script+json",
            "targets": ["application/vnd.google-apps.script"]
        }, {
            "source": "application/vnd.openxmlformats-officedocument.presentationml.slideshow",
            "targets": ["application/vnd.google-apps.presentation"]
        }, {
            "source": "application/vnd.ms-powerpoint.template.macroenabled.12",
            "targets": ["application/vnd.google-apps.presentation"]
        }, {
            "source": "text/csv",
            "targets": ["application/vnd.google-apps.spreadsheet"]
        }, {
            "source": "application/vnd.oasis.opendocument.presentation",
            "targets": ["application/vnd.google-apps.presentation"]
        }, {
            "source": "image/jpg",
            "targets": ["application/vnd.google-apps.document"]
        }, {
            "source": "text/richtext",
            "targets": ["application/vnd.google-apps.document"]
        }],
        "name": "Projet SI",
        "etag": "\"An1NegRH_Q1WfUh5qt1ofDI9qPI/eptdid4XdApOklF3hBw6nlRQUL8\"",
        "user": {
            "permissionId": "09758181586404959543",
            "isAuthenticatedUser": true,
            "emailAddress": "projetsi.drive@gmail.com",
            "kind": "drive#user",
            "displayName": "Projet SI"
        },
        "maxUploadSizes": [{
            "size": "10485760",
            "type": "application/vnd.google-apps.document"
        }, {
            "size": "104857600",
            "type": "application/vnd.google-apps.spreadsheet"
        }, {
            "size": "104857600",
            "type": "application/vnd.google-apps.presentation"
        }, {
            "size": "2097152",
            "type": "application/vnd.google-apps.drawing"
        }, {
            "size": "5242880000000",
            "type": "application/pdf"
        }, {
            "size": "5242880000000",
            "type": "*"
        }],
        "exportFormats": [{
            "source": "application/vnd.google-apps.form",
            "targets": ["application/zip"]
        }, {
            "source": "application/vnd.google-apps.document",
            "targets": ["application/rtf", "application/vnd.oasis.opendocument.text", "text/html", "application/pdf", "application/zip", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text/plain"]
        }, {
            "source": "application/vnd.google-apps.drawing",
            "targets": ["image/svg+xml", "image/png", "application/pdf", "image/jpeg"]
        }, {
            "source": "application/vnd.google-apps.spreadsheet",
            "targets": ["text/csv", "application/x-vnd.oasis.opendocument.spreadsheet", "application/zip", "application/pdf", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"]
        }, {
            "source": "application/vnd.google-apps.script",
            "targets": ["application/vnd.google-apps.script+json"]
        }, {
            "source": "application/vnd.google-apps.presentation",
            "targets": ["application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/pdf", "text/plain"]
        }]
    }
}
```

### /mkdir

#### Description:

Créé un dossier

#### URI:

`http://localhost:8080/kiwishare/mkdir`

### Paramètres:

**path** = le répetoire à générer.<br>
_Exemple_ : `localhost:8080/kiwishare/mkdir?path=DIR`

#### Methode:

**GET**

#### Retour:

Exemple :

```json
{
    "dropbox": {
        "thumb_exists": false,
        "path": "/DIR",
        "rev": "35484e58e3",
        "size": "0 bytes",
        "read_only": false,
        "bytes": 0,
        "is_dir": true,
        "modifier": null,
        "root": "dropbox",
        "icon": "folder",
        "modified": "Sun, 15 May 2016 17:57:34 +0000",
        "revision": 53
    },
    "drive": {
        "lastModifyingUserName": "Projet SI",
        "shared": false,
        "owners": [{
            "permissionId": "09758181586404959543",
            "isAuthenticatedUser": true,
            "emailAddress": "projetsi.drive@gmail.com",
            "kind": "drive#user",
            "displayName": "Projet SI"
        }],
        "mimeType": "application/vnd.google-apps.folder",
        "title": "DIR",
        "quotaBytesUsed": "0",
        "lastModifyingUser": {
            "permissionId": "09758181586404959543",
            "isAuthenticatedUser": true,
            "emailAddress": "projetsi.drive@gmail.com",
            "kind": "drive#user",
            "displayName": "Projet SI"
        },
        "copyable": false,
        "iconLink": "https://ssl.gstatic.com/docs/doclist/images/icon_11_collection_list_1.png",
        "alternateLink": "https://docs.google.com/folderview?id=0B_i6_IkJyufETHd6bGpGZ3IzRDQ&usp=drivesdk",
        "id": "0B_i6_IkJyufETHd6bGpGZ3IzRDQ",
        "modifiedByMeDate": "2016-05-15T17:57:36.216Z",
        "lastViewedByMeDate": "2016-05-15T17:57:36.216Z",
        "writersCanShare": true,
        "userPermission": {
            "role": "owner",
            "kind": "drive#permission",
            "etag": "\"An1NegRH_Q1WfUh5qt1ofDI9qPI/xs_WJpWflnVAP95FsSXGAXUBQXM\"",
            "id": "me",
            "type": "user",
            "selfLink": "https://www.googleapis.com/drive/v2/files/0B_i6_IkJyufETHd6bGpGZ3IzRDQ/permissions/me"
        },
        "kind": "drive#file",
        "editable": true,
        "ownerNames": ["Projet SI"],
        "version": "152",
        "selfLink": "https://www.googleapis.com/drive/v2/files/0B_i6_IkJyufETHd6bGpGZ3IzRDQ",
        "labels": {
            "starred": false,
            "hidden": false,
            "restricted": false,
            "viewed": true,
            "trashed": false
        },
        "markedViewedByMeDate": "1970-01-01T00:00:00.000Z",
        "appDataContents": false,
        "explicitlyTrashed": false,
        "createdDate": "2016-05-15T17:57:36.216Z",
        "modifiedDate": "2016-05-15T17:57:36.216Z",
        "spaces": ["drive"],
        "etag": "\"An1NegRH_Q1WfUh5qt1ofDI9qPI/MTQ2MzMzNTA1NjIxNg\"",
        "parents": [{
            "parentLink": "https://www.googleapis.com/drive/v2/files/0APi6_IkJyufEUk9PVA",
            "isRoot": true,
            "kind": "drive#parentReference",
            "id": "0APi6_IkJyufEUk9PVA",
            "selfLink": "https://www.googleapis.com/drive/v2/files/0B_i6_IkJyufETHd6bGpGZ3IzRDQ/parents/0APi6_IkJyufEUk9PVA"
        }]
    }
}
```

### /rm

#### Description:

Supprime un fichier

#### URI:

`http://localhost:8080/kiwishare/rm`

### Paramètres:

**path** = le répetoire à générer.<br>
_Exemple_ : `localhost:8080/kiwishare/rm?path=DIR`

#### Methode:

**GET**

#### Retour:

Exemple :

```json
{
    "dropbox": {
        "thumb_exists": false,
        "rev": "36484e58e3",
        "modifier": null,
        "icon": "folder_gray",
        "revision": 54,
        "path": "/DIR",
        "is_deleted": true,
        "size": "0 bytes",
        "read_only": false,
        "bytes": 0,
        "is_dir": true,
        "root": "dropbox",
        "modified": "Sun, 15 May 2016 17:59:53 +0000"
    },
    "drive": {
        "err": "Unable to parse json null"
    }
}
```

### /mv

#### Description:

Déplace un fichier.

#### URI:

`http://localhost:8080/kiwishare/mv`

### Paramètres:

**from** = Le fichier à déplacer.<br>
**to** = La nouvelle destination.<br>
_Exemple_ : `localhost:8080/kiwishare/rm?from=FROM&to=TO`

#### Methode:

**GET**

#### Retour:

Exemple :

```json
{
    "dropbox": {
        "thumb_exists": false,
        "rev": "38484e58e3",
        "modifier": null,
        "icon": "page_white_code",
        "revision": 56,
        "path": "/generate/test.cpp",
        "client_mtime": "Sat, 14 May 2016 12:45:22 +0000",
        "size": "181 bytes",
        "read_only": false,
        "mime_type": "text/x-c++src",
        "bytes": 181,
        "is_dir": false,
        "root": "dropbox",
        "modified": "Sun, 15 May 2016 18:04:31 +0000"
    },
    "drive": {
        "lastModifyingUserName": "Projet SI",
        "shared": true,
        "downloadUrl": "https://doc-0o-6o-docs.googleusercontent.com/docs/securesc/0ihp9lv9fmpj0uolejr0nncm7av0o7fi/4dq6sjr1smkb22rqdin4ui5n4n1reobt/1463335200000/09758181586404959543/09758181586404959543/0B_i6_IkJyufEZjRuNU1waEVlV2c?e=download&gd=true",
        "owners": [{
            "permissionId": "09758181586404959543",
            "isAuthenticatedUser": true,
            "emailAddress": "projetsi.drive@gmail.com",
            "kind": "drive#user",
            "displayName": "Projet SI"
        }],
        "mimeType": "text/x-c++src",
        "title": "test.cpp",
        "thumbnailLink": "https://lh5.googleusercontent.com/d31MHIoy6qQ-hM-wI6eZn9NDwP3djNaFT67pMZEUfOHvQRyc-hwleQ6F4cEkV9MsXb-Jpw=s220",
        "quotaBytesUsed": "182",
        "md5Checksum": "1d18732b31be7db4e9564123b59b325c",
        "lastModifyingUser": {
            "permissionId": "09758181586404959543",
            "isAuthenticatedUser": true,
            "emailAddress": "projetsi.drive@gmail.com",
            "kind": "drive#user",
            "displayName": "Projet SI"
        },
        "copyable": true,
        "iconLink": "https://ssl.gstatic.com/docs/doclist/images/generic_app_icon_16.png",
        "fileExtension": "cpp",
        "alternateLink": "https://drive.google.com/file/d/0B_i6_IkJyufEZjRuNU1waEVlV2c/view?usp=drivesdk",
        "id": "0B_i6_IkJyufEZjRuNU1waEVlV2c",
        "modifiedByMeDate": "2016-05-15T18:04:33.216Z",
        "lastViewedByMeDate": "2016-05-15T18:04:33.216Z",
        "webContentLink": "https://docs.google.com/uc?id=0B_i6_IkJyufEZjRuNU1waEVlV2c&export=download",
        "writersCanShare": true,
        "userPermission": {
            "role": "owner",
            "kind": "drive#permission",
            "etag": "\"An1NegRH_Q1WfUh5qt1ofDI9qPI/WXWhaYt95qsJO4ngtokJgNRKbMY\"",
            "id": "me",
            "type": "user",
            "selfLink": "https://www.googleapis.com/drive/v2/files/0B_i6_IkJyufEZjRuNU1waEVlV2c/permissions/me"
        },
        "kind": "drive#file",
        "editable": true,
        "ownerNames": ["Projet SI"],
        "version": "156",
        "selfLink": "https://www.googleapis.com/drive/v2/files/0B_i6_IkJyufEZjRuNU1waEVlV2c",
        "labels": {
            "starred": false,
            "hidden": false,
            "restricted": false,
            "viewed": true,
            "trashed": false
        },
        "markedViewedByMeDate": "1970-01-01T00:00:00.000Z",
        "appDataContents": false,
        "explicitlyTrashed": false,
        "createdDate": "2016-05-15T08:03:33.126Z",
        "fileSize": "182",
        "modifiedDate": "2016-05-15T18:04:33.216Z",
        "spaces": ["drive"],
        "etag": "\"An1NegRH_Q1WfUh5qt1ofDI9qPI/MTQ2MzMzNTQ3MzIxNg\"",
        "originalFilename": "main.cpp",
        "parents": [{
            "parentLink": "https://www.googleapis.com/drive/v2/files/0B_i6_IkJyufEcVdwQjJNd1VNa28",
            "isRoot": false,
            "kind": "drive#parentReference",
            "id": "0B_i6_IkJyufEcVdwQjJNd1VNa28",
            "selfLink": "https://www.googleapis.com/drive/v2/files/0B_i6_IkJyufEZjRuNU1waEVlV2c/parents/0B_i6_IkJyufEcVdwQjJNd1VNa28"
        }],
        "headRevisionId": "0B_i6_IkJyufEUE1vNVE4UzR1ZnBPQmdpRm1tSCtsVTQ5ckhBPQ"
    }
}
```

### /share

#### Description:

Obtient l'url de partage des différents services.

#### URI:

`http://localhost:8080/kiwishare/share`

### Paramètres:

**path** = Le fichier à partager.<br>
_Exemple_ : `localhost:8080/kiwishare/share?path=PATH`

#### Methode:

**GET**

#### Retour:

Exemple :

```json
{
    "dropbox": {
        "expires": "Tue, 01 Jan 2030 00:00:00 +0000",
        "visibility": "PUBLIC",
        "url": "https://db.tt/bOXJWgA1"
    },
    "drive": {
        "link": "https://drive.google.com/file/d/0B_i6_IkJyufEZjRuNU1waEVlV2c/view?usp=drivesdk"
    }
}
```

### /tree

#### Description:

Obtient l'arborescence des fichiers de tous les services.

#### URI:

`http://localhost:8080/kiwishare/tree`

#### Methode:

**GET**

#### Retour:

Exemple :

```json
{
    "files": [{
        "path": "add"
    }, {
        "path": "c.cpp"
    }, {
        "path": "generate"
    }, {
        "path": "img.jpg"
    }, {
        "path": "llun"
    }, {
        "path": "main.cpp"
    }, {
        "path": "Prise en main de Dropbox.pdf"
    }, {
        "path": "test.jpg"
    }, {
        "path": "to/y"
    }, {
        "path": "to"
    }, {
        "path": "yolo"
    }, {
        "path": "DIRECTORY"
    }, {
        "path": "generate/test.jpg"
    }, {
        "path": "mkdir"
    }, {
        "path": "generate/Screenshot from 2016-05-05 15-51-51.png"
    }]
}
```
