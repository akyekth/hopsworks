#!/bin/bash
# Deploy the frontend to the glassfish home directory and run bower
export PORT=22104
export WEBPORT=25104
export SERVER=bbc1.sics.se
export key=private_key

scp johsn@bbc1.sics.se:/home/johsn/delaTwo/hopsworks-chef/.vagrant/machines/default/virtualbox/private_key .

scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -o IdentitiesOnly=yes -i $key -P ${PORT} -r vagrant@${SERVER}:/srv/dela/*.csv /Users/jsvhqr/Tests
