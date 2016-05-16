import subprocess

TXT = "rsc/demo.txt"
remoteTXT = "demo/demo.txt"
remoteMvTXT = "demoMv/demo.txt"

print(
'''############################################################
###                          UPLOAD                      ###
############################################################'''
)

p = subprocess.Popen("curl http://localhost:8080/kiwishare/mkdir?path=demo", shell=True, stdout=subprocess.PIPE)
p.wait()
p = subprocess.Popen("curl -F \"file=@"+TXT+"\" http://localhost:8080/kiwishare/put?path="+remoteTXT, shell=True, stdout=subprocess.PIPE)
p.wait()

print("Upload " + TXT + " to " + remoteTXT)

print(
'''############################################################
###                           MOVE                       ###
############################################################'''
)

p = subprocess.Popen("curl http://localhost:8080/kiwishare/mkdir?path=demoMv", shell=True, stdout=subprocess.PIPE)
p.wait()
p = subprocess.Popen("curl http://localhost:8080/kiwishare/mv?from="+ remoteTXT + "&to=" + remoteMvTXT, shell=True, stdout=subprocess.PIPE)
p.wait()

print("Move " + remoteTXT + " to " + remoteMvTXT)
