#!/bin/sh

cd "$(dirname $0)"

# Create CA certificate and key

openssl genrsa -out ca.priv 2048

MSYS_NO_PATHCONV=1 openssl req -x509 -new -nodes -sha256 -days 1825 -key ca.priv \
        -out ca.crt -subj "/C=US/O=modelapi/CN=ca"

# Create server certificate and key

openssl genrsa -out modelapi.priv 2048

MSYS_NO_PATHCONV=1 openssl req -new -key modelapi.priv -out modelapi.csr \
        -subj "/C=US/O=modelapi/CN=modelapi.com"

cat <<EOF >modelapi.ext
subjectAltName = @alt_names
[alt_names]
DNS.1 = localhost
DNS.2 = modelapi.com
DNS.3 = www.modelapi.com
EOF

openssl x509 -req -in modelapi.csr -CA ca.crt -CAkey ca.priv \
        -out modelapi.crt -CAcreateserial -days 1825 -sha256 \
        -extfile modelapi.ext

openssl pkcs12 -export -in modelapi.crt -inkey modelapi.priv \
        -out modelapi.p12 -certfile modelapi.crt \
        -password pass:changeit -name modelapi

openssl pkcs12 -in modelapi.p12 -out modelapi.pub \
        -clcerts -nokeys -passin pass:changeit

openssl pkcs12 -in modelapi.p12 -out modelapi.pem \
        -nodes -passin pass:changeit