import subprocess

print(
'''############################################################
###                     TREE (NOT MERGED)                ###
############################################################'''
)

p = subprocess.Popen("curl http://localhost:8080/kiwishare/tree", shell=True, stdout=subprocess.PIPE)
out, err = p.communicate()
print(out)

print(
'''############################################################
###                       TREE (MERGED)                  ###
############################################################'''
)

p = subprocess.Popen("curl http://localhost:8080/kiwishare/tree?merge=1", shell=True, stdout=subprocess.PIPE)
out, err = p.communicate()
print(out)
