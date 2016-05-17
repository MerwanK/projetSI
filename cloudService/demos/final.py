import subprocess
import json
import time

# Note: YEAP THIS IS UGLY, just for demo time... JUST DON'T REPRODUCE OR YOU WILL... (write a fun sentence)

print('''1. You need to get 2 tokens. So this app will open some pages on firefox.
2. Accept the first page for Dropbox and close firefox
3. Accept the second page for Google drive and close firefox
''')

time.sleep(5)

print(
'''############################################################
###                          DROPBOX                     ###
############################################################'''
)
p = subprocess.Popen("firefox 'https://www.dropbox.com/1/oauth2/authorize?response_type=code&client_id=n3ukjy5rxfu5dnc&redirect_uri=http://localhost:8080/kiwishare/callbackDropbox'", shell=True, stdout=subprocess.PIPE)
p.wait()

print(
'''############################################################
###                           DRIVE                      ###
############################################################'''
)
p = subprocess.Popen("firefox 'https://accounts.google.com/o/oauth2/v2/auth?response_type=code&scope=https://www.googleapis.com/auth/drive&client_id=462659653340-ckldp4re47tg7slfj8q3tsvc6ur59657.apps.googleusercontent.com&redirect_uri=http://localhost:8080/kiwishare/callbackDrive'", shell=True, stdout=subprocess.PIPE)
p.wait()

menuIn = 0
while menuIn >= 0:
    try:
        if menuIn is 0:
            print('''Menu :
            0. Show this menu
            1. Put a file
            2. Put an encrypted file (encrypt, get file, then put)
            3. Get info from file
            4. Decrypt local file (get a file from google drive without any interface is just ...)
            5. Get file from google
            6. Share file
            7. Make a directory
            8. Get the tree
            9. Delete a file
            10. Get info from space
            else Move File
            ''')
        elif menuIn == 1:
            local = input("path (local file):")
            remote = input("remote url:")
            print("Upload:\n")
            p = subprocess.Popen("curl -F \"file=@"+local+"\" http://localhost:8080/kiwishare/put?path="+remote, shell=True, stdout=subprocess.PIPE)
            p.wait()
            print("Done!\n")
        elif menuIn == 2:
            local = input("path (local file):")
            localEncrypted = input("wanted path for encrypted file:")
            remote = input("remote url:")
            print("Encrypt:\n")
            p = subprocess.Popen("curl -F \"file=@" + local + "\" http://localhost:8080/kiwiencrypt/encrypt > " + localEncrypted, shell=True, stdout=subprocess.PIPE)
            p.wait()
            print("Done!\nUpload:\n")
            p = subprocess.Popen("curl -F \"file=@"+localEncrypted+"\" http://localhost:8080/kiwishare/put?path="+remote, shell=True, stdout=subprocess.PIPE)
            p.wait()
            print("Done!\n")
        elif menuIn == 3:
            remote = input("remote url:")
            print("Get:\n")
            p = subprocess.Popen("curl http://localhost:8080/kiwishare/get?path="+remote, shell=True, stdout=subprocess.PIPE)
            out, err = p.communicate()
            print(out)
            print("Done\n")
        elif menuIn == 4:
            encrypted = input("Encrypted file (local):")
            decrypted = input("Wanted path for unencrypted content:")
            print("Decrypt:\n")
            p = subprocess.Popen("curl -F \"file=@"+encrypted+"\" http://localhost:8080/kiwiencrypt/decrypt > "+decrypted, shell=True, stdout=subprocess.PIPE)
            p.wait()
            print("Done:\n")
        elif menuIn == 5:
            remote = input("remote url:")
            print("Get:\n")
            p = subprocess.Popen("curl http://localhost:8080/kiwishare/get?path="+remote, shell=True, stdout=subprocess.PIPE)
            out, err = p.communicate()
            jsonRes = json.loads(out.decode("utf_8").replace("'","\""))
            print(jsonRes)
            println("Via dropbox: " + jsonRes['dropbox']['download_url'] + '\n') #TODO fail ? DAFUK
            println("Via drive: " + jsonRes['drive']['webContentLink'] + '\n')#TODO fail ? DAFUK
            print("Done\n")
        elif menuIn == 6:
            remote = input("remote url:")
            print("Get:\n")
            p = subprocess.Popen("curl http://localhost:8080/kiwishare/share?path="+remote, shell=True, stdout=subprocess.PIPE)
            out, err = p.communicate()
            jsonRes = json.loads(out.decode("utf_8").replace("'","\""))
            print(jsonRes)
            println("Via dropbox: " + jsonRes['dropbox']['url'] + '\n') #TODO fail ? DAFUK
            println("Via drive: " + jsonRes['drive']['link'] + '\n')#TODO fail ? DAFUK
            print("Done\n")
        elif menuIn == 7:
            remote = input("remote url:")
            print("Mkdir:\n")
            p = subprocess.Popen("curl http://localhost:8080/kiwishare/mkdir?path="+remote, shell=True, stdout=subprocess.PIPE)
            p.wait()
            print("Done!\n")
        elif menuIn == 8:
            merge = input("merge (1/0/true/false):")
            print("Tree:\n")
            p = subprocess.Popen("curl http://localhost:8080/kiwishare/tree?merge="+merge, shell=True, stdout=subprocess.PIPE)
            out, err = p.communicate()
            print(out)
            print("Done!\n")
        elif menuIn == 9:
            remote = input("remote url:")
            print("DELETE:\n")
            p = subprocess.Popen("curl http://localhost:8080/kiwishare/rm?path="+remote, shell=True, stdout=subprocess.PIPE)
            p.wait()
            print("Done!\n")
        elif menuIn == 10:
            print("INFOS:\n")
            p = subprocess.Popen("curl http://localhost:8080/kiwishare/info", shell=True, stdout=subprocess.PIPE)
            out, err = p.communicate()
            jsonRes = json.loads(out.decode("utf_8").replace("'","\""))
            print(out)
            println("Quota dropbox: " + str(int(jsonRes['dropbox']['quota_info']['normal'])/1000) + '/' + str(int(jsonRes['dropbox']['quota_info']['quota'])/1000) + '\n')#TODO fail ? DAFUK
            println("Quota drive: " + jsonRes['drive']['quotaBytesUsedAggregate'] + '/' + jsonRes['drive']['quotaBytesTotal'] + '\n')#TODO fail ? DAFUK
            print("Done!\n")
        else:
            fromUrl = input("from:")
            toUrl = input("to:")
            print("Move:\n")
            p = subprocess.Popen("curl 'http://localhost:8080/kiwishare/mv?from="+fromUrl+"&to="+toUrl+"'", shell=True, stdout=subprocess.PIPE)
            p.wait()
            print("Done!\n")
    except:
        print("oops\n")
        menuIn = 0

    var = input("Please enter something: ")
    menuIn = int(var)
