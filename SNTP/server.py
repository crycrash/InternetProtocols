import sys
import socket
import struct
from client import SNTPClient
import threading


class SNTPServer:
    """RFC 4330"""
    header = '> B B B B I I 4s Q Q Q Q'
    leap_indicator = 0  # что-то про високосный год??
    version = 4
    mode = 4  # сервер
    stratum = 1
    first_octet = leap_indicator << 6 | version << 3 | mode
    client_request = '\x1b' + 47 * '\0'

    def __init__(self, host='127.0.0.1', port=123, offset_user=0):
        self.host = host
        self.port = port
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.server_socket.bind((self.host, self.port))
        self.offset = int(offset_user)

    def get_current_time(self):
        sntp_client = SNTPClient("time.apple.com")
        data = sntp_client.request_time()
        return data[0] + self.offset

    def handle_client_request(self, data, address):
        sntp_client = SNTPClient("time.apple.com")
        data = sntp_client.request_time()
        receive = data[1]
        current_time = self.get_current_time()

        response = self.generate_sntp_packet(current_time, data[1], receive)

        self.server_socket.sendto(response, address)

    def run(self):
        while True:
            data, address = self.server_socket.recvfrom(1024)
            if data:
                thread = threading.Thread(target=self.handle_client_request,
                                          args=(data, address))
                thread.start()

    def generate_sntp_packet(self, current_time, originate, receive):
        ntp_timestamp = self.convert_to_ntp(current_time)
        orig = self.convert_to_ntp(originate + self.offset)
        rec = self.convert_to_ntp(receive + self.offset)
        return struct.pack(self.header, self.first_octet,
                           self.stratum, 0, 0, 0, 0, b'', 0,
                           orig, ntp_timestamp,
                           rec)

    @staticmethod
    def convert_to_ntp(unix_time):
        return int((unix_time + 2208988800) * 2 ** 32)


if __name__ == '__main__':
    if len(sys.argv) == 2:
        offset = int(sys.argv[1])
        server = SNTPServer(offset_user=offset)
    else:
        server = SNTPServer()
    server.run()