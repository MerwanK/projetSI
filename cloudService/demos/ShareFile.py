import subprocess

TXT = "rsc/demo.txt"
remoteTXT = "demoShr/demo.txt"

print(
'''############################################################
###                          UPLOAD                      ###
############################################################'''
)

p = subprocess.Popen("curl http://localhost:8080/kiwishare/mkdir?path=demoShr", shell=True, stdout=subprocess.PIPE)
p.wait()
p = subprocess.Popen("curl -F \"file=@"+TXT+"\" http://localhost:8080/kiwishare/put?path="+remoteTXT, shell=True, stdout=subprocess.PIPE)
p.wait()

print("Upload " + TXT + " to " + remoteTXT)

print(
'''############################################################
###                          SHARE                       ###
############################################################'''
)

p = subprocess.Popen("curl http://localhost:8080/kiwishare/share?path=" + remoteTXT, shell=True, stdout=subprocess.PIPE)
out, err = p.communicate()
print(out.decode("utf-8"))
