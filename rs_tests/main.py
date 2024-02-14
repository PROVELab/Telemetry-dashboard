import rs

coder = rs.RSCoder(64, 16)
normal_codeword = "".join([chr(ord('A') + i % 26) for i in range(16)])
encoded = coder.encode(normal_codeword)
print(normal_codeword)
print("Encoded to: ", encoded)

messed = b"324" + b"asd" + encoded[10:14] + "fa" + encoded[16:20] + b"idk" + encoded[23:28] + b"lmaoi" + encoded[33:]
print("Modified 16 bytes, deleted 4 bytes to:", messed, len(messed))
dec = coder.decode(messed)
print("decoded to: ", dec, len(dec))

# removed_some = encoded[16:]
# dec = coder.decode(removed_some)
# print(dec, len(dec))