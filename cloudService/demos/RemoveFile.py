import subprocess

TXT = "rsc/demo.txt"
remoteTXT = "demoRm/demo.txt"

print(
'''############################################################
###                          UPLOAD                      ###
############################################################'''
)

p = subprocess.Popen("curl http://localhost:8080/kiwishare/mkdir?path=demoRm", shell=True, stdout=subprocess.PIPE)
p.wait()
p = subprocess.Popen("curl -F \"file=@"+TXT+"\" http://localhost:8080/kiwishare/put?path="+remoteTXT, shell=True, stdout=subprocess.PIPE)
p.wait()

print("Upload " + TXT + " to " + remoteTXT)

print(
'''############################################################
###                          REMOVE                      ###
############################################################'''
)

p = subprocess.Popen("curl http://localhost:8080/kiwishare/rm?path=" + remoteTXT, shell=True, stdout=subprocess.PIPE)
p.wait()

print("DELETE " + remoteTXT)
