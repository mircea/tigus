For the parsing algorithm o behave as expected the plain text file should respect a template:

1 The file must end with two or more blank lines

2 Each question must begin with it's tags (properly marked at the beginning of the line 
with the "@" marker) otherwise an exception will be thrown

3 Each answer must be placed on one line only (properly marked at the beginning of the line
with the "+" or "-" marker)

4 There must be no empty line or a line not beginning with the marker between the answer lines 
(exception will be thrown)

5 The questions are separated by one or more empty lines

6 The question text can have any number of lines and it can also include blank lines

7 Empty lines can be placed anywhere in the file except between the answers

8 Any empty lines separating the question text from the answers will be included in the
question text

9 Although non common types !(chapter || difficulty || desc) of tags are not printed 
with the debug they are still added in the question object


For debug puporses uncomment line number 113.

This is a sample of how an imput file should look like:
////////////////////////////////////////////////////////////////////////////////////////
@question challenge
@chapter 10

Care dintre urmatoarele nu sunt active intr-o sesiune de password recovery realizata cu init=/bin/sh dat ca parametru kernel-ului la boot?
+ serviciul de logging ksyslogd
+ serverul grafic X
+ utilitarele getty de pe terminale
+ configuratiile statice de retea din /etc/network/interfaces



@question challenge
@chapter 10

Fie urmatoarea succesiune de comenzi:
<pre>
stefan@ubuntu-phane:~/testing$ umask 0345
stefan@ubuntu-phane:~/testing$ touch readme.txt
stefan@ubuntu-phane:~/testing$ chmod o+x readme.txt
stefan@ubuntu-phane:~/testing$ chmod u=w,g+r,o-rx readme.txt
</pre>

Presupunand ca "readme.txt" nu exista in prealabil, ce permisiuni va avea acesta dupa executia comenzilor?

+ "--w-rw--w-"
- "drw--w-rw-"
- "--w-r-x---"
- "d-w-r-x---"



@question challenge
@chapter 10

Fie urmatorul output:
<pre>
Dec  9 20:35:37 ubuntu-phane sudo:   stefan : TTY=pts/1 ; PWD=/home/stefan/work/eclipse-new/Tema4SO ; USER=root ; COMMAND=/usr/bin/apt-get install electric-fence
</pre>

Din ce fisier poate face parte?

+ /var/log/auth.log
- /etc/sudoers
- /var/log/dmesg
- /var/log/user.log


//////////////////////////////////////////////////////////////////////////////////////////


And this is the parsing result:
//////////////////////////////////////////////////////////////////////////////////////////
     [java] ------------NEW QUESTION-------------
     [java] ----------------TAGS-----------------
     [java] chapter-10 ,
     [java] ----------------TEXT-----------------
     [java] Care dintre urmatoarele nu sunt active intr-o sesiune de password recovery realizata cu init=/bin/sh dat ca parametru kernel-ului la boot?
     [java] ---------------ANSWER----------------
     [java] Correct: serviciul de logging ksyslogd
     [java] Correct: serverul grafic X
     [java] Correct: utilitarele getty de pe terminale
     [java] Correct: configuratiile statice de retea din /etc/network/interfaces
     [java] 
     [java] ------------NEW QUESTION-------------
     [java] ----------------TAGS-----------------
     [java] chapter-10 ,
     [java] ----------------TEXT-----------------
     [java] Fie urmatoarea succesiune de comenzi:
     [java] <pre>
     [java] stefan@ubuntu-phane:~/testing$ umask 0345
     [java] stefan@ubuntu-phane:~/testing$ touch readme.txt
     [java] stefan@ubuntu-phane:~/testing$ chmod o+x readme.txt
     [java] stefan@ubuntu-phane:~/testing$ chmod u=w,g+r,o-rx readme.txt
     [java] </pre>
     [java] 
     [java] Presupunand ca "readme.txt" nu exista in prealabil, ce permisiuni va avea acesta dupa executia comenzilor?
     [java] 
     [java] ---------------ANSWER----------------
     [java] Correct: "--w-rw--w-"
     [java] Incorrect: "drw--w-rw-"
     [java] Incorrect: "--w-r-x---"
     [java] Incorrect: "d-w-r-x---"
     [java] 
     [java] ------------NEW QUESTION-------------
     [java] ----------------TAGS-----------------
     [java] chapter-10 ,
     [java] ----------------TEXT-----------------
     [java] Fie urmatorul output:
     [java] <pre>
     [java] Dec  9 20:35:37 ubuntu-phane sudo:   stefan : TTY=pts/1 ; PWD=/home/stefan/work/eclipse-new/Tema4SO ; USER=root ; COMMAND=/usr/bin/apt-get install electric-fence
     [java] </pre>
     [java] 
     [java] Din ce fisier poate face parte?
     [java] 
     [java] ---------------ANSWER----------------
     [java] Correct: /var/log/auth.log
     [java] Incorrect: /etc/sudoers
     [java] Incorrect: /var/log/dmesg
     [java] Incorrect: /var/log/user.log
/////////////////////////////////////////////////////////////////////////////////////////