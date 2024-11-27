# start mysql on docker
docker run -d \
  --name mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -v mysql_data:/var/lib/mysql \
  -p 3306:3306 \
  5d2fb452c483ffe15d4ec362a904fd285ab1f4dedd98b0a38060c87f1d98f582
