#include <openssl/conf.h>
#include <openssl/evp.h>
#include <openssl/err.h>
#include <string.h>

void handleErrors(void)
{
    // printf("Eroare la criptare/decriptare!\n");
    // ERR_print_errors_fp(stderr);
    // abort();
}

int decrypt(int mode, unsigned char *ciphertext, int ciphertext_len, unsigned char *key,
            unsigned char *iv, unsigned char *plaintext)
{
    EVP_CIPHER_CTX *ctx;

    int len, plaintext_len;

    /* Create and initialise the context */
    if (!(ctx = EVP_CIPHER_CTX_new())) 
        handleErrors();

    /* Initialise the decryption operation. */
    if(mode == 1) {
        if (1 != EVP_DecryptInit_ex(ctx, EVP_aes_256_ecb(), NULL, key, NULL))
            handleErrors();
    }
    else if (1 != EVP_DecryptInit_ex(ctx, EVP_aes_256_cbc(), NULL, key, iv))
            handleErrors();
    /*
     * Provide the message to be decrypted, and obtain the plaintext output.
     * EVP_DecryptUpdate can be called multiple times if necessary.
     */
    if (1 != EVP_DecryptUpdate(ctx, plaintext, &len, ciphertext, ciphertext_len))
        handleErrors();
    
    plaintext_len = len;

    /*
     * Finalise the decryption. Further plaintext bytes may be written at
     * this stage.
     */
    if (1 != EVP_DecryptFinal_ex(ctx, plaintext + len, &len))
        handleErrors();
    
    plaintext_len += len;

    /* Clean up */
    EVP_CIPHER_CTX_free(ctx);

    return plaintext_len;
}

int encrypt(int mode, unsigned char *plaintext, int plaintext_len, unsigned char *key,
            unsigned char *iv, unsigned char *ciphertext)
{
    EVP_CIPHER_CTX *ctx;
    int len, ciphertext_len;

    /* Create and initialise the context */
    if (!(ctx = EVP_CIPHER_CTX_new()))
        handleErrors();

    /* Initialise the encryption operation */
    if(mode == 1) {
        if (1 != EVP_EncryptInit_ex(ctx, EVP_aes_256_ecb(), NULL, key, NULL))
            handleErrors();
    }
    else if (1 != EVP_EncryptInit_ex(ctx, EVP_aes_256_cbc(), NULL, key, iv))
        handleErrors();

    /*
     * Provide the message to be encrypted, and obtain the encrypted output.
     * EVP_EncryptUpdate can be called multiple times if necessary
     */
    if (1 != EVP_EncryptUpdate(ctx, ciphertext, &len, plaintext, plaintext_len))
        handleErrors();
    ciphertext_len = len;

    /*
     * Finalise the encryption. Further ciphertext bytes may be written at
     * this stage.
     */
    if (1 != EVP_EncryptFinal_ex(ctx, ciphertext + len, &len))
        handleErrors();
    ciphertext_len += len;

    /* Clean up */
    EVP_CIPHER_CTX_free(ctx);

    return ciphertext_len;
}

unsigned char *convertToHex(unsigned char *key)
{
    if (strlen(key) == 0) return 0;

    int i = 0, j = 0;
    unsigned char *hexa = (unsigned char *)malloc(64);

    for (i, j; i < strlen(key); i++, j += 4)
        sprintf((char *)hexa + j, "\\x%x", key[i]);
    i = strlen(key) - 1;
    for (i, j; i < 16; i++, j += 4)
        sprintf((char *)hexa + j, "\\x%x", ' ');
    hexa[64] = '\0';

    return hexa;
}

int main(void)
{
    unsigned char *key = convertToHex("cryptanalysis");
    unsigned char *iv = (unsigned char *)"\x01\x02\x03\x04\x05\x06\x07\x08\x09\x0a\x0b\x0c\x0d\x0e\x0f";

    int mode;
    printf("1 - ECB\n2 - CBC\n");
    scanf("%d", &mode);

    FILE *fp = fopen("plaintext.txt", "r");
    unsigned char plaintext[255];
    fgets(plaintext, 255, (FILE *)fp);
    printf("Plaintext is:\n%s\n", plaintext);

    unsigned char ciphertext[128], decryptedtext[128];
    int decryptedtext_len, ciphertext_len;

    // ---- Criptare
    ciphertext_len = encrypt(mode, plaintext, strlen((char *)plaintext), key, iv, ciphertext);
    FILE *cipher_file = fopen("criptotext.txt", "w");
    BIO_dump_fp(cipher_file, (const char *)ciphertext, ciphertext_len);

    // ---- Decriptare
    decryptedtext_len = decrypt(mode, ciphertext, ciphertext_len, key, iv, decryptedtext);
    decryptedtext[decryptedtext_len] = '\0';
    printf("Decrypted text is: \n%s\n", decryptedtext);


    // ---- Brute force: try every word from the dictionary as key */

    int iteratii = 0;
    FILE *word_file = fopen("word_dictionary.txt", "r");
    size_t word_len, len = 0;
    char *word = NULL;
    while ((word_len = getline(&word, &len, word_file)) != -1)
    {
        word[word_len - 1] = '\0';
        // printf("Trying word: %s\n", word);
        if (word_len < 17)
        {
            iteratii++;
            unsigned char secondCipherText[128];
            int secondCipherText_len;
            while (word_len != 16)
            {
                strcat(word, " ");
                word_len++;
            }
            unsigned char* key2 = convertToHex((unsigned char *)word);
            decryptedtext_len = decrypt(mode, ciphertext, ciphertext_len, key2, iv, decryptedtext);
            decryptedtext[decryptedtext_len] = '\0';

            // frequency of letters
            int c = 0, nr=0;
            while(decryptedtext[c] != '\0') {
                if(decryptedtext[c] >= 'a' && decryptedtext[c] <= 'z') nr++;
                c++;
            }
            if(nr > 20) {
                // printf("%s\n", decryptedtext);
                printf("\nKey is: %s\n", key2);
                printf("Base word for key: %s\n", word);
                break;
            }
        }
    }
    printf("Nr iteratii: %d\n", iteratii);

    return 0;
}