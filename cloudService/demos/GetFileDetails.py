import subprocess

TXT = "rsc/demo.txt"
remoteTXT = "demo/demo.txt"

print(
'''############################################################
###                           UPLOAD                    ###
############################################################'''
)
p = subprocess.Popen("curl http://localhost:8080/kiwishare/mkdir?path=demo", shell=True, stdout=subprocess.PIPE)
p.wait()
p = subprocess.Popen("curl -F \"file=@"+TXT+"\" http://localhost:8080/kiwishare/put?path="+remoteTXT, shell=True, stdout=subprocess.PIPE)
p.wait()
print("[INFO]Le fichier a été envoyé : "+remoteTXT)

print(
'''############################################################
###                          DOWNLOAD                    ###
############################################################'''
)

p = subprocess.Popen("curl http://localhost:8080/kiwishare/get?path="+remoteTXT, shell=True, stdout=subprocess.PIPE)
out, err = p.communicate()
print(out)
