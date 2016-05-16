import subprocess
import json

originPng = "rsc/demo.png"
encryptedPNG = "rsc/encryptedPNG.gpg"
decryptedPNG = "rsc/decryptedPNG.png"
remoteEncryptedPNG = "demo/encryptedPNG.gpg"

print(
'''############################################################
###                           ENCRYPT                    ###
############################################################'''
)
p = subprocess.Popen("curl -F \"file=@" + originPng + "\" http://localhost:8080/kiwiencrypt/encrypt > " + encryptedPNG, shell=True, stdout=subprocess.PIPE)
p.wait()

print("[INFO]Le fichier chiffré se trouve dans : " + encryptedPNG)

print(
'''############################################################
###                           UPLOAD                    ###
############################################################'''
)
p = subprocess.Popen("curl http://localhost:8080/kiwishare/mkdir?path=demo", shell=True, stdout=subprocess.PIPE)
p.wait()
p = subprocess.Popen("curl -F \"file=@"+encryptedPNG+"\" http://localhost:8080/kiwishare/put?path="+remoteEncryptedPNG, shell=True, stdout=subprocess.PIPE)
p.wait()
print("[INFO]Le fichier a été envoyé : "+remoteEncryptedPNG)

print(
'''############################################################
###                          DOWNLOAD                    ###
############################################################'''
)

p = subprocess.Popen("curl http://localhost:8080/kiwishare/get?path="+remoteEncryptedPNG, shell=True, stdout=subprocess.PIPE)
out, err = p.communicate()
resp = json.loads(out.decode("utf-8"))
urlDrive = resp['drive']['webContentLink']

print("[INFO]Le fichier est disponible ici sur google drive : " + urlDrive)

print(
'''############################################################
###                          DECRYPT                     ###
############################################################'''
)

p = subprocess.Popen("curl -F \"file=@"+encryptedPNG+"\" http://localhost:8080/kiwiencrypt/decrypt > "+decryptedPNG, shell=True, stdout=subprocess.PIPE)
p.wait()

print("[INFO]Le fichier est déchiffré ici : "+decryptedPNG)
