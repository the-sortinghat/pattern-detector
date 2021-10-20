FROM node:16.11-alpine3.12

ENV PORT=3000 \
    HOST=0.0.0.0 \
    APP_PATH=/usr/src/app

WORKDIR ${APP_PATH}

EXPOSE ${PORT}

COPY package.json yarn.lock ./

CMD yarn start

RUN yarn install

COPY . ./
