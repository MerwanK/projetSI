import subprocess

print(
'''############################################################
###                          INFO                        ###
############################################################'''
)

p = subprocess.Popen("curl http://localhost:8080/kiwishare/info", shell=True, stdout=subprocess.PIPE)
out, err = p.communicate()
print(out.decode("utf-8"))
