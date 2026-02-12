# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

![Sequence Diagram](sequence.png)
[link-to-diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQHPCMykoKp+h-Ds0KPMB4lUEiMAIEJ4oYoJwkEkSYCkm+hi7jS+4MkyU76XOnl3kuwowAA4kyZawFo8gwAAZt4MxCpoOjtsO-L1BkYA+O8G6wK+zrub6+X1HA8QoCAGpqTsxr2CcaA5IlqWLhZVkZVlaA5XlnaCo1o4wIecgoM+8Tnpe149fewWPgGo1bm57Y6aWDnihkqgAZgC0gdU-ojMs+l7N8FFUfWixLJpW3wsmyCpjAMAAJANLhTidMMMA7YR5afCdsGXkdyEnTAVyYCqwYano66VQCMDQBFnI3gFDpJoVnbdr2-ZzYjHY1K60yXtASAAF4oBwUYxnGhRaRjolpk4ACMBE5qoebzNBRYlvUYMwxW0MQ7sgP0Y2DGeN4fj+F4KDoDEcSJGLEsOb4WCiYKoH1A00gRvxEbtBG3Q9HJqgKcMh2Ieg6HwpUC31EbSHrWZGHNS61lCfLI3wcbaCufl3W3mljJgENLuUW7-m8oFlTLjAvvGpeRhbsYKXe-SMAopl2UUTH8hwyHCP212ydtdNL6zZ7HlZ4nHAoNwx6XgHv3BwuApBelRwQDQUd6rF8WJXHGM9bUZA+PuMCZ-Xm3I+QA8jtu7kfrbpb9-uNuwnbFn+vPk8U5dJRgCrj3dCMVzA+qbf6mgEDMOKQowBiNn2Iy67y5WZqWKS3cl-XKN9sPg8Y25H9o8XK9SzMhxnqPGhNibRhQLGBSptMJXWwo0dMNNnrtTegzJmBYxis2gPUHwID4hgKJqdUw-N+ZC2Yv4FE65-DYHFBqfiaJQpKg0IrQBKsQqax1jVOYhsfpu1gTPJepYrYmw2jneoyAcghWYTXIOU8vbw0Tr7f2IjCjjUbhHcKacorACSvHRRvU86p2jjor+gVxFJyZPnbRW5D5qj0T3BOvUeyf3URYlx-8uo-3OtjCihCIGkxgRvSoVNEG0xQa9ZY6D8ws2LDghU+D-HEP5ujKkTiJFomkTmDEddv5h2CpHE0CAI7MIcW-LyliU7tW4YYSRWA3FI0sg7Gp1lGEgAgH2MqZN5EYwtiUuYrT6rtM6eAGBYi2FMLmAwnIAjN7XXurvF6b0amqALA0cYNSACS0gCw03CMEQIIJNjxF1CgN0nJ9ogmSKANU5zIJfWWDUgAckqE6FwYCdHeQLRiwsAgcAAOxuCcCgJwMQIzBDgFxAAbPACchgsmGCKPAsSjTJKtA6FwpUvDXZISzM8pUZ0JJmweEIy2fCkIrBGPiuYpkhFKyKn1OFCLZFIRgCsalXIelpIMd5P21dVG5NDkKXlx9066Nfty0uhirHGPbqYhpmMrJGPajYjOdiymSvfjADxZjs6NKsjq9GEzgF+KgATImJMoHdOCfAZFKskERLQfyDBsS2YJNNea3YANvmpJkOkxlR4UDMo5YKhG+T6hTRaRqv1PLKn5xqUgBiCrf79JQImnpgivwMiVOm8ZqLSwJoYja0JD08J70eUqbZuz9mBG9WQpiItLAVxspsSWSAEhgCbX2CAraABSEAL4IpiNc8qSKt70qJSrJozIZI9BqdiwOuLRjYAQMAJtUA4AdOgGsLZ0hCWJnNrPMlOL0CUpXWuygm6bJQB3ZWvdi8vwTqaV2GAAArAdaBmWqNUqu9dV7t2pu2R7LxmqKnKP5eS9AobFzhs0cwVV4rkqONjcq0Vpi7GyhDKKyA1sJUxqlX-XVTV9UO0NQA-NvjcZmvAZa6B8Zi12puvdB1MBsGljwR68B3zk0MtQwhrl+GtUgFKuVYNd7oMN1g5HFp6a8O91TemjDLSOAQDUGgAA5MwK0aJo3yZ1TxseZGvGZrhPUPU3hW0ACEQxOTUGAWj3S82Y2eNZjgtmcgOYUkPBjW97VltY3E0sHMUSEjs980+6Ik7tsSFDGKiqHYlTKhqUqKIYCQBgAYQwSA4vl31lQvqW7YDZGNFQcEuhuACZTTUlwIYOBNF-ZifSP6L0bsK6SG6HXOtdZur6+TfgopBqVBiGpY0nETVwdgAbqaYAX2MLp-1a9+REYbhYxb6gM2HtJePBeTnlbbbvBiR6wHExzIQaWp6r06JNnISLLwa620dru-KRAwZYDAGwCuwgeQChD1YRRxoasNZax1nNzrFNNtZpgPEW27mwA5MwJ1sRJGX0gG4HgBQH3kAgG+2geHCOOu9f9eXSug0+z+3ax19RsHidMkMEU7DbsNCdbk-61aC9OsGeffUKgA6GJg+LqBkc9QafHj7G6VQ8OOdjY0SLunfYIpivxzdFnsa2eTylwY0eXOYA8-TUr+LBVBMVNl4GBACKJcU5ulT4VRgK608rMUlZ+uVcEYyxPJbGupVa6srrwW-PjMQ9M3G7Ka2BT692z4-bHuCfnTgb5xou9LvfKAA)
## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
