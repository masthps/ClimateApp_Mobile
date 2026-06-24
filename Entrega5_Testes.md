# Entrega 5 - Testes e ajustes finais

Esta entrega reúne ajustes simples de usabilidade e um checklist de testes manuais para validar o funcionamento principal do ClimateApp.

## Ajustes realizados

- Correção e padronização dos textos com acentuação.
- Melhoria das mensagens de erro para cidade não encontrada, falha de conexão e falha no processamento dos dados.
- Exibição de aviso quando o aplicativo usa dados salvos no SQLite para acesso offline.

## Checklist de testes manuais

- [ ] Abrir o aplicativo e verificar se São Paulo é carregada automaticamente.
- [ ] Buscar uma cidade válida, como Rio de Janeiro, Curitiba ou Salvador.
- [ ] Buscar uma cidade com acento, como São Paulo ou Goiânia.
- [ ] Buscar uma cidade inválida e confirmar se uma mensagem amigável é exibida.
- [ ] Confirmar se o clima atual aparece com temperatura, sensação, umidade e vento.
- [ ] Confirmar se a previsão de 5 dias aparece corretamente.
- [ ] Desligar a internet após carregar uma cidade.
- [ ] Buscar novamente uma cidade já carregada e confirmar se os dados salvos no SQLite são exibidos.
- [ ] Confirmar se aparece o aviso de dados offline.
- [ ] Verificar se o aplicativo não fecha sozinho durante a abertura, busca e exibição da previsão.

## Resultado esperado

O aplicativo deve permanecer estável, apresentar mensagens claras ao usuário e permitir o acesso à última previsão salva quando não houver conexão disponível.
