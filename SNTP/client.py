import socket
import struct
import time


class SNTPClient:

    def __init__(self, ntp_server):
        self.client_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.ntp_server = ntp_server

    def request_time(self):
        sss = SNTPClient('time.apple.com')
        transmit = sss.enter_request_time()
        request = '\x1b' + 47 * '\0'  # режим клиента
        data_user = request.encode('utf-8')
        self.client_socket.sendto(data_user, (self.ntp_server, 123))
        data_user, address = self.client_socket.recvfrom(1024)
        if data_user:
            return struct.unpack('!12I', data_user)[10] - 2208988800, transmit

    def enter_request_time(self):
        request = '\x1b' + 47 * '\0'  # режим клиента
        data_user = request.encode('utf-8')
        self.client_socket.sendto(data_user, (self.ntp_server, 123))
        data_user, address = self.client_socket.recvfrom(1024)
        if data_user:
            return struct.unpack('!12I', data_user)[10] - 2208988800


if __name__ == '__main__':
    a = SNTPClient("127.0.0.1")
    print(str(time.ctime(a.request_time()[0])))