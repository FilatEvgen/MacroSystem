# Authenticate Module

## Описание
Authenticate Module зволяет выполнять авторизацию пользователя с помощью таких сервисов как: VK ID, Mail.ru, OK.ru

## Запуск
Чтобы запустить модуль, необходимо вызвать функцию configureAuthorizationRouting() в блоке размещения маршрутов Ktor сервера, а так-же разместить в .env дирректории путь к файлу конфигурации сервисных приложений, с ключом AUTH_CONFIG_PATH

#### config.json
```json
{
  "vk_provider": {
    "client_id": 1234567,
    "redirect_uri": "https://myDomain.com/"
  }
}
```

### Идентификатор провайдеров
VK ID - vk_id

## Запросы

### GET
```bash
curl -X GET "http://localhost:8080/v1/authenticate/url" \
   -H "Content-Type: application/x-www-form-urlencoded" \
   -d "provider={provider_id}"
```
#### Параметры
provider (Обязательный параметр) - строка содержащая идентификтор сервиса, с помощью которого необходимо провести авторизацию

#### Ответ
Успешным ответом считается собранная ссылка для перехода на сервис авторизации

### POST
```bash
curl -X POST "http://localhost:8080/v1/authenticate/callback" \
   -H "Content-Type: application/x-www-form-urlencoded" \
   -d "provider={provider_id}" \
   -d "code={code}" \
   -d "device_id={device_id}" \
   -d "state={state}"
```
#### Параметры
provider (Обязательный параметр) - строка содержащая идентификтор сервиса, с помощью которого необходимо провести авторизацию \

code, device_id, state (Обязательные параметры) - параметры перехваченные со страницы перенаправления

#### Ответ
200
```json
{
	"user": {
		"user_id": "123456789",
		"first_name": "Никита",
		"last_name": "Ч.",
		"phone": "user_phone",
		"avatar": "avatar_uri",
		"email": "user@email.com"
	}
}
```

## В разработке
Работа движется в направлении унификации метода авторизации для большинства популярных сервисов, таких как ok.ru, mail.ru, google.com, apple_id