import sys
import socket
from threading import Thread


def tcp_scanner(port, host):
    try:
        tcp = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        tcp.settimeout(1.5)
        if not tcp.connect_ex((host, port)):
            print('TCP Open ' + str(port) + ' ' + define_protocol(tcp))
            tcp.close()
        else:
            print('TCP Close ' + str(port))
    except Exception:
        pass


def udp_scanner(port, host):
    try:
        udp = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        udp.settimeout(0.06)
        udp.sendto(b'', (host, port))
        data, _ = udp.recvfrom(1024)
        print('UDP Open ' + str(port) + define_protocol(udp))
    except Exception:
        # print('UDP Close ' + str(port))
        pass


def start(start_port_protocol, finish_port_protocol, protocol, host):
    for port in range(start_port_protocol, finish_port_protocol + 1):
        if protocol == 'TCP':
            thread = Thread(target=tcp_scanner,
                            args=[port, host])
            thread.start()

        elif protocol == "UDP":
            thread = Thread(target=udp_scanner,
                            args=[port, host])
            thread.start()


def define_protocol(sock):
    protocol = ' i dont know this protocol'
    try:
        sock.send(b'1213\r\n\r\n')
        data = sock.recv(1024)
        if b'SMTP' in data:
            protocol = 'SMTP'
        if ((b'POP3' in data or b'+OK' in data or b'-ERR' in data or b'USER' in
            data) or b'PASS' in data or b'STAT' in data or b'LIST' in data or
                b'RETR' in data
                or b'TOP' in data or b'DELE' in data or b'QUIT' in data):
            protocol = 'POP3'
        if b'CRLF' in data or b'A000' in data or b'IMAP' in data:
            protocol = 'IMAP'
        if b'HTTP' in data or b'GET' in data:
            protocol = 'HTTP'
        if b'NTP' in data:
            protocol = 'NTP'
        if b'DNS' in data:
            protocol = 'DNS'
    finally:
        return protocol


if __name__ == '__main__':
    if len(sys.argv) == 5:
        start_port = int(sys.argv[1])
        finish_port = int(sys.argv[2])
        if (start_port < 0) or (start_port > 65536) or (finish_port < 0) or (
                finish_port > 65536):
            print('Неверный номер порта')
            exit(-1)
        start(start_port, finish_port, sys.argv[3], sys.argv[4])
    else:
        print('Неправильный ввод аргументов')
        exit(-1)

"""
sudo python scaner.py 1 65000 TCP localhost - порты TCP
sudo python scaner.py 587 600 TCP smtp.mail.ru - SMTP
sudo python scaner.py 1 450 TCP smtp.mail.ru - SMTP
sudo python scaner.py 64000 65000 UDP localhost - порты UDP
sudo python scaner.py 1 100 TCP mail.ru - HTTP 
sudo python scaner.py 120 150 TCP 80.87.194.88 - IMAP
sudo python scaner.py 105 115 TCP outlook.office.com - POP3
sudo python scaner.py 1 150 TCP outlook.office.com - все сразу
"""