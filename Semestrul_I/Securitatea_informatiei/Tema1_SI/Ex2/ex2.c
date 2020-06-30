#include <openssl/conf.h>
#include <openssl/evp.h>
#include <string.h>
#include <openssl/err.h>
#include <openssl/sha.h>

void handleErrors(void) {
    ERR_print_errors_fp(stderr);
    abort();
}

void digest_md5(const unsigned char *message, size_t message_len, unsigned char **digest, unsigned int *digest_len){
	EVP_MD_CTX *mdctx;
	if((mdctx = EVP_MD_CTX_create()) == NULL) handleErrors();
	if(1 != EVP_DigestInit_ex(mdctx, EVP_md5(), NULL)) handleErrors();
	if(1 != EVP_DigestUpdate(mdctx, message, message_len)) handleErrors();
	if((*digest = (unsigned char *) OPENSSL_malloc(EVP_MD_size(EVP_md5()))) == NULL) handleErrors();
	if(1 != EVP_DigestFinal_ex(mdctx, *digest, digest_len)) handleErrors();
	EVP_MD_CTX_destroy(mdctx);
}

void digest_sha256(const unsigned char *message, size_t message_len, unsigned char **digest, unsigned int *digest_len){
	EVP_MD_CTX *mdctx;
	if((mdctx = EVP_MD_CTX_create()) == NULL) handleErrors();
	if(1 != EVP_DigestInit_ex(mdctx, EVP_sha256(), NULL)) handleErrors();
	if(1 != EVP_DigestUpdate(mdctx, message, message_len)) handleErrors();
	if((*digest = (unsigned char *) OPENSSL_malloc(EVP_MD_size(EVP_sha256()))) == NULL) handleErrors();
	if(1 != EVP_DigestFinal_ex(mdctx, *digest, digest_len)) handleErrors();
	EVP_MD_CTX_destroy(mdctx);
}

int main(void)
{
	FILE *file1 = fopen("file1.txt", "r");
	fseek(file1, 0, SEEK_END);
	long file1_size = ftell(file1);
	fseek(file1, 0, SEEK_SET);

	unsigned char *text1 = malloc(file1_size);
	fread(text1, file1_size, 1, file1);
	text1[file1_size] = '\0';
	fclose(file1);

	FILE *file2 = fopen("file2.txt", "r");
	fseek(file2, 0, SEEK_END);
	long file2_size = ftell(file2);
	fseek(file2, 0, SEEK_SET);

	unsigned char *text2 = malloc(file2_size);
	fread(text2, file2_size, 1, file2);
	text2[file2_size] = '\0';
	fclose(file2);

	// printf("%s\n", text1);
	// printf("%s\n", text2);

	// ---- SHA256

	unsigned char *ciphertext1, *ciphertext2;
	unsigned int ciphertext1_len, ciphertext2_len;
	digest_sha256(text1, strlen(text1), &ciphertext1, &ciphertext1_len);
	FILE *h1_sha256 = fopen("h1_sha256.txt", "w");
	fputs(ciphertext1, h1_sha256);
	fclose(h1_sha256);

	digest_sha256(text2, strlen(text2), &ciphertext2, &ciphertext2_len);
	FILE *h2_sha256 = fopen("h2_sha256.txt", "w");
	fputs(ciphertext2, h2_sha256);
	fclose(h2_sha256);

	unsigned char buffer1, buffer2;
	int count = 0;
	FILE *fileSha1 = fopen("h1_sha256.txt", "r");
	FILE *fileSha2 = fopen("h2_sha256.txt", "r");
	while(fread(&buffer1, 1, 1, fileSha1) && fread(&buffer2, 1, 1, fileSha2))
		if(buffer1 == buffer2) count++;
	printf("SHA256 - Nr octeti identici: %d\n", count);
	fclose(fileSha1);
	fclose(fileSha2);


	// --- MD5

	digest_md5(text1, strlen(text1), &ciphertext1, &ciphertext1_len);
	FILE *h1_md5 = fopen("h1_md5.txt", "w");
	fputs(ciphertext1, h1_md5);
	fclose(h1_md5);

	digest_md5(text2, strlen(text2), &ciphertext2, &ciphertext2_len);
	FILE *h2_md5 = fopen("h2_md5.txt", "w");
	fputs(ciphertext2, h2_md5);
	fclose(h2_md5);

	FILE *fileMd1 = fopen("h1_md5.txt", "rb");
	FILE *fileMd2 = fopen("h2_md5.txt", "rb");
	count = 0;
	while(fread(&buffer1, 1, 1, fileMd1) && fread(&buffer2, 1, 1, fileMd2))
		if(buffer1 == buffer2) count++;
	printf("MD5 - Nr octeti identici: %d\n", count);
	fclose(fileMd1);
	fclose(fileMd2);

	return 0;
}