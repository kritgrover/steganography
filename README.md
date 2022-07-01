# Steganography
An application made using Kotlin that takes an image file as an input, encodes it with a message within the pixels along with a password which is used for decoding.

## What is Steganography?
Steganography is the technique of hiding secret data within an ordinary, non-secret, file or message in order to avoid detection; the secret data is then extracted at its destination. The use of steganography can be combined with encryption as an extra step for hiding or protecting data. 

In modern digital steganography, data is first encrypted or obfuscated in some other way and then inserted, using a special algorithm, into data that is part of a particular file format such as a JPEG image, audio or video file. The secret message can be embedded into ordinary data files in many different ways. One technique is to hide data in bits that represent the same color pixels repeated in a row in an image file. By applying the encrypted data to this redundant data in some inconspicuous way, the result will be an image file that appears identical to the original image but that has "noise" patterns of regular, unencrypted data.

Source: www.techtarget.com

## Implementation

### Basic Idea
Images can be viewed as 2-dimensional arrays. A 24-bit image offers more than 16 million colors for every pixel. If the RGB color scheme is used, then 8 bits (values 0–255) represent the Red, Green, and Blue colors. If the least significant bits of these 8-bit values change, the difference in the image will remain unnoticed. This is exactly what is used for hiding the message in this project. The program sets the **least significant bit** for the blue color of each pixel of the input image to the value of encrypted byte of the message.

With the `BufferedImage` class methods, the image pixels can be accessed as a **2-dimensional array**. IO Exceptions are handled.

### Encoding
Steganography is about hiding information in such a way that no-one would ever guess there's a secret message hidden right before their eyes. The method we are going to use for concealing a message in an image is based on slight color changes that can’t be detected.

The message data can be inserted at the positions of the least significant bits of each color value of each pixel. That makes 3 bits per pixel and a total of 3 * [image width] * [image height] bits for the whole image. That isn't used as it would be more efficient to have an algorithm that picks which bits to use. The message to hide has the String type and UTF-8 charset. As a result, the message can be in any language. Specifically, we will add three bytes with values 0, 0, 3 (or 00000000 00000000 00000011 in the binary format). When the program encounters these bytes, it will know that it has reached the end of the message.

### Encrypting with Password
We could make it even more complex: the bit selection can be based on a password so that the configuration is different every time the password is changed.

Exclusive OR (XOR) is a logical operation whose output is only true when its inputs differ. XOR can also be used on data bits where `1` stands for `true` and `0` stands for `false`. Below you can find both the **XOR truth table** (on the left) with the input and output values of the XOR operation and the XOR bitwise operation (on the right).

XOR has an interesting mathematical feature: _A XOR B = C and C XOR B = A_. If _A_ is the message and **B** the password, then _C_ is the encrypted message. Using _B_ and _C_, we can reconstruct _A_.

The program reads the password string and converts it to a Bytes Array. The first message byte will be XOR encrypted using the first password byte, the second message byte will be XOR encrypted with the second password byte, and so on. If the password is shorter than the message, then after the last byte of the password, the first byte of the password should be used again.

Three Bytes with values 0, 0, 3 should be added to the encrypted Bytes Array. If the image size is adequate for holding the Bytes array, the result is hidden in the image.

### Decoding
When the `show` command is triggered and the filename is input, the user is prompted for the password with the message `Password:`. The image should open and the encrypted Bytes Array should be reconstructed. The program stops reading it when the bytes with the values 0, 0, 3 are found. The last three bytes should be removed and the encrypted Bytes Array should be decrypted using the password. Finally, the message should be restored to the String type, and the program should print the message on a new line.
