CFLAGS=-std=c89 -pedantic

master: master.o node.o user.o utils.o
	gcc master.o node.o user.o utils.o -o master

master.o: master.c master.h
	gcc -c $(CFLAGS) master.c

node.o: node.c node.h
	gcc -c $(CFLAGS) node.c

user.o: user.c user.h
	gcc -c $(CFLAGS) user.c

utils.o: utils.c utils.h
	gcc -c $(CFLAGS) utils.c

clean:
	rm -f *.o master

run:
	./master
