# Отчет по лабораторной работе

## Грамматика

```
S    -> type name (args);
args -> arg arg_ | Eps
arg  -> type star name
arg_ -> , type star name arg_ | Eps
star -> * star | Eps
type -> word
name -> word
```

**В ней нет ни левой рекурсии, ни правого ветвления.**

1. Нельзя найти вывода вида A ->* Ab
2. Нельзя найти правила вида A -> xB | xC

## First & Follow

| Nonterminal |     First    |      Follow      | 
| ----------- | ------------ | -----------------|
| S           |     word     |         $        |
| args        |   word, eps  |        ')'       |
| arg         |     word     |     ',', ')'     |
| arg_        |   ',', eps   |        ')'       |
| star        |   '*', eps   |       word       |
| type        |     word     |     word, '*'    |
| name        |     word     |   '(', ',', ')'  |

## Структура

1. В Lexer получаем следующий токен.
2. В Parser с помощью найденных First и Follow анализируем начальное и следующее состояние и совершаем рекурсивный спуск.

## Тесты

|      Тест                        |    Что проверяем        
|----------------------------------|------------------------------
|0. int da();                      |args -> Eps
|1. int da(string ok);             |arg_ -> Eps
|2. int da(string ok, boolean yes);|arg_ -> , type star name arg_
|3. int da(string **ok);           |star -> * star 
