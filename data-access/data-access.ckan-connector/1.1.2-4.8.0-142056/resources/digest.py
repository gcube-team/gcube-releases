from hashlib import md5

def calculate_digest(ip, timestamp, secret, userid, tokens, user_data):
    secret = maybe_encode(secret)
    userid = maybe_encode(userid)
    tokens = maybe_encode(tokens)
    user_data = maybe_encode(user_data)
    digest0 = md5(encode_ip_timestamp(ip, timestamp) + secret + userid + '\0' + tokens + '\0' + user_data).hexdigest()
    digest = md5(maybe_encode(digest0) + secret).hexdigest()
    return digest


if type(chr(1)) == type(''): #pragma NO COVER Python < 3.0
    def ints2bytes(ints):
        return ''.join(map(chr, ints))
else: #pragma NO COVER Python >= 3.0
    def ints2bytes(ints):
        return bytes(ints)

def encode_ip_timestamp(ip, timestamp):
    ip_chars = ints2bytes(map(int, ip.split('.')))
    t = int(timestamp)
    ts = ((t & 0xff000000) >> 24,
          (t & 0xff0000) >> 16,
          (t & 0xff00) >> 8,
          t & 0xff)
    ts_chars = ints2bytes(ts)
    return ip_chars + ts_chars


def maybe_encode(s, encoding='utf8'):
    if not isinstance(s, type('')):
        s = s.encode(encoding)
    return s

