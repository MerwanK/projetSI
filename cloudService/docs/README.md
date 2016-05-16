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

Vous pouvez ensuite créer le fichier de config comme il suit :

```json
{
  "gpg_key":"ID",
  "gpg_pass":"PASSPHRASE"
}
```

De plus, vous n'êtes pas obligé de spécifier l'entrée _gpg_key_. Vous pouvez utiliser la clé par défaut de votre système. Il vous faut pour ceci rajouter ces 3 lignes au fichier _~/.gnupg/gpg.conf_ :

```bash
default-key IDDELACLE
default-recipient some-user-id
default-recipient-self
```

## Lancement d'une instance

Voici les commandes pour lancer le serveur :

```bash
mvn compile
mvn jetty:run
```

## Manipulation des services

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

#### Description:

Envoie un fichier

#### URI:

`http://localhost:8080/kiwishare/put`

#### Paramètres:

**path**= le chemin de destination.<br>
dans les headers : **file** le contenu du fichier.<br>
_Exemple_ : `curl -F "file=@/home/AmarOk/main.cpp" http://localhost:8080/kiwishare/put?path=main.cpp`

#### Methode:

**POST**

#### Retour:

Exemple :

```json
{
    "dropbox": {
        "send": "{\"revision\": 68, \"bytes\": 182, \"thumb_exists\": false, \"rev\": \"44484e58e3\", \"modified\": \"Mon, 16 May 2016 11:15:58 +0000\", \"shareable\": false, \"mime_type\": \"text/x-c++src\", \"path\": \"/voila.cpp\", \"is_dir\": false, \"size\": \"182 bytes\", \"root\": \"dropbox\", \"client_mtime\": \"Mon, 16 May 2016 11:15:58 +0000\", \"icon\": \"page_white_code\"}"
    },
    "drive": {
        "lastModifyingUserName": "Projet SI",
        "shared": false,
        "downloadUrl": "https://doc-08-6o-docs.googleusercontent.com/docs/securesc/0ihp9lv9fmpj0uolejr0nncm7av0o7fi/0huu4lbnclu67ukk47l7p43kn7ucrtdh/1463392800000/09758181586404959543/09758181586404959543/0B_i6_IkJyufEWHN3WXBqd3FlN2c?e=download&gd=true",
        "owners": [{
            "permissionId": "09758181586404959543",
            "isAuthenticatedUser": true,
            "emailAddress": "projetsi.drive@gmail.com",
            "kind": "drive#user",
            "displayName": "Projet SI"
        }],
        "mimeType": "application/octet-stream",
        "title": "voila.cpp",
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
        "iconLink": "https://ssl.gstatic.com/docs/doclist/images/icon_10_generic_list.png",
        "fileExtension": "cpp",
        "alternateLink": "https://drive.google.com/file/d/0B_i6_IkJyufEWHN3WXBqd3FlN2c/view?usp=drivesdk",
        "id": "0B_i6_IkJyufEWHN3WXBqd3FlN2c",
        "modifiedByMeDate": "2016-05-16T11:16:00.282Z",
        "lastViewedByMeDate": "2016-05-16T11:16:00.282Z",
        "webContentLink": "https://docs.google.com/uc?id=0B_i6_IkJyufEWHN3WXBqd3FlN2c&export=download",
        "writersCanShare": true,
        "userPermission": {
            "role": "owner",
            "kind": "drive#permission",
            "etag": "\"An1NegRH_Q1WfUh5qt1ofDI9qPI/DjK8DQKO9gmmMevZlNjBXL3ZC8Y\"",
            "id": "me",
            "type": "user",
            "selfLink": "https://www.googleapis.com/drive/v2/files/0B_i6_IkJyufEWHN3WXBqd3FlN2c/permissions/me"
        },
        "kind": "drive#file",
        "editable": true,
        "ownerNames": ["Projet SI"],
        "version": "300",
        "selfLink": "https://www.googleapis.com/drive/v2/files/0B_i6_IkJyufEWHN3WXBqd3FlN2c",
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
        "createdDate": "2016-05-16T11:16:00.282Z",
        "fileSize": "182",
        "modifiedDate": "2016-05-16T11:16:00.282Z",
        "spaces": ["drive"],
        "etag": "\"An1NegRH_Q1WfUh5qt1ofDI9qPI/MTQ2MzM5NzM2MDI4Mg\"",
        "originalFilename": "voila.cpp",
        "parents": [{
            "parentLink": "https://www.googleapis.com/drive/v2/files/0APi6_IkJyufEUk9PVA",
            "isRoot": true,
            "kind": "drive#parentReference",
            "id": "0APi6_IkJyufEUk9PVA",
            "selfLink": "https://www.googleapis.com/drive/v2/files/0B_i6_IkJyufEWHN3WXBqd3FlN2c/parents/0APi6_IkJyufEUk9PVA"
        }],
        "headRevisionId": "0B_i6_IkJyufEdzBvcFozS1dSaGFhcEJxT3A2RGJFUlMzWlJVPQ"
    }
}
```

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

## Encrypt all the things!

### /encrypt

#### Description:

Chiffre le contenu d'un fichier.

#### URI:

`http://localhost:8080/kiwiencrypt/encrypt`

#### Methode:

**POST**

#### Paramètres:

**file** en paramètre de la requête. Contient le contenu du fichier.<br>
_Exemple_ : `curl -F "file=@/home/AmarOk/main.cpp" http://localhost:8080/kiwiencrypt/encrypt?name=temp`

#### Retour:

Le fichier chiffré. Exemple :

```text
-----BEGIN PGP MESSAGE-----
Version: GnuPG v1

hQIMA5SXZViidrq2AQ//Y9kD/uOTKM2VJ+kGrd5VypbG0HmF1XACWo1cYYTefZ5A
mxdfvol8BMNafmTc5hjk1DKfypGGAOix2h7kwg/WKf3w+qJoZ/n6PT9WTrWMm7eN
2TxmYGM+XNNfNsPr8NTQ8tuge/0Bs74LXE/CrGi2i7IRU7IpDTGSghEXAGtVIa9V
Xl0iF9AZlX2fy53b4IqpwNQ8iQDKELOKDMycVF0oObkDYslvoMpoN4FnVMUulG6g
Mt4MYJC2y3hWLC+GgaeHi4VGjzV/K6tPOZvODrneTVgaVPsB0r3im56pJYkZVsOZ
cDsR1ZUNrAOV3LYcPHC2yQAZqJhdwwBSt9tHoRPfmjIU5ycl3Ciil7BSHdbrRtxC
axy6u3NFNM7D/7sw106sDGwTksGbqwkj3tJ9bjqPw+QKSIm4AIprnze013OVi6QL
i0q8rsBhPYq4f58LP4alQvAesBnNmzIyGzZNDNfnmphWeHNt0EzqiNG9dRRqOnmM
IIxvbjmrqV8yp3VmSPtXCwGRrTtQra4xCX5zmgE/Ii8qmINS9Di08zE8U0uPrwVF
WLj4Nie86jeGlxKEzLA7ZPTR4w0SRMVuxYLqjZSp/l6GvRYthepx+vUI3lT/38Uv
ah8Hk5ShvR+jB7dMmHyQlQLO8eosmLMIMcEpv2IlhRGLdVQDzswBq1V722H3GpTS
6QEhdi4jRPvdIyKFKGoZKP5uc+2d8mIDQER7/W3+ivRUVak17VUublTMYOknuwCz
12/7YoVF0cgfRzqAoAaGCl4O+Vscj2FYmrMg6ahJ4av32LXX/z4PBwGXeMlVX2BA
5VWMPjXPTlyUx6cYRvqR8zSqPuyD1C3JXSaipzuE95x/YWC5clYA3QOfVERWjJkR
lJZdpyFYfUtg4af1mJlOEdZz+5UO51GCwoy0efh9IIqdeVvsSCCgv1Qf83b6UTZ7
d5DIXGCMA6wKbreMM7+nu4AEHOC7qcakj1H7wxWYHQTU8hQ3OdoYGi4OYYZnw/3X
8nSFUJqBlWXUxJLkLG8ehaXemj4BIrLUJ5MMmpW+lOBMpUU7KvapIvEg7kjL/B5q
L+D1wdvmgTHn5mAK6N7mKuEYm7MaMgrO5QEK/t656ZWS1Uel8AXEVTYDXJoRcvQb
etRMsM45y7hfnxiFg8lVjXSZm2wolRkZRrO6jSyyNqLkNXw6GhuygsyZOoN5xt/B
TU8oglSa7/IeeE8fKzheANy89SPo11o6t7ByDI1i2eAW8DcCaWJw37MJCdoLK7Pm
jemLu57hWQEE/9aayXNtQEQpE2tUhB6aPx+X9QmQicsK4fyacNfjIcIWtS+nZUtM
GGtni4GG5UneZON3sEprkMmxn480ja32tZAkqjX6E+JswEr0/pNWQBedhsz4yqxK
3QS3IH/tVXWY3GyU4Ne5fqpLPBBmzGQ5vDB3DQDO18c6tk6ANDpx1SjoBDhAZ9iz
Bt2wGmj3lfWNZOFlr46kX38Ruo+fJN3ITN1/8QdajrRs9QlSz2i4WNPb7PWrOIoP
1w8IGVdp6aW0Tk6M3k9kvB1Eq8OpLbVpYzPx2+2ogWU/FEFAb3fQrwUDEXPHRe3d
8JILS1WeMmZKePQ1Mb4l3z9NzG3nZA40SnAfPlyZyTezVUXhJCQYoey/2ciQfyyp
ZrvMWV2XwAoSmKoWKztuhQpa32rXkdFh8pWZ61yobytKRoFnnhQjoQpqUtQGOYlp
rwE49S6M3sUywdbbvg==
=lVuZ
-----END PGP MESSAGE-----
```

### /decrypt

#### Description:

Déchiffre le contenu d'un fichier.

#### URI:

`http://localhost:8080/kiwiencrypt/decrypt`

#### Methode:

**POST**

#### Paramètres:

**file** en paramètre de la requête. Contient le contenu du fichier.<br>
_Exemple_ : `curl -F "file=@/home/AmarOk/main.gpg" http://localhost:8080/kiwiencrypt/decrypt?name=temp`

#### Retour:

Le fichier déchiffré. Exemple :

```text
#include <stdlib.h>
#include <iostream>

int main() {
    int a = 0;
    // Will the next line be executed????????????????/
    a++;
    std::cout << a << std::endl;
    return 0;
}
```
