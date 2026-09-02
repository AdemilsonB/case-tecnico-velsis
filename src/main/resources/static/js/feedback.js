// Faixa de aviso no topo das telas. A diferença entre sucesso e erro não é só
// de cor: a mensagem de sucesso confirma algo que já terminou e some sozinha
// depois de alguns segundos, enquanto a de erro fica até a pessoa corrigir o
// que está errado. Um erro que desaparece no meio da leitura esconde
// justamente a informação de que o usuário precisa.

const TEMPO_SUCESSO = 4000;

// Fora das funções para que uma mensagem nova cancele o desaparecimento
// agendado pela anterior
let timerFaixa = null;

function mostrarSucesso(faixa, mensagem) {
  exibirFaixa(faixa, mensagem, 'sucesso');
  // Em formulário longo o botão Salvar fica embaixo e a faixa em cima; sem
  // isso a confirmação apareceria fora da área visível
  faixa.scrollIntoView({ block: 'nearest' });
  timerFaixa = setTimeout(function () {
    faixa.hidden = true;
  }, TEMPO_SUCESSO);
}

function mostrarErro(faixa, mensagem) {
  exibirFaixa(faixa, mensagem, 'erro');
}

function esconderFaixa(faixa) {
  clearTimeout(timerFaixa);
  faixa.hidden = true;
}

function exibirFaixa(faixa, mensagem, tipo) {
  clearTimeout(timerFaixa);
  faixa.textContent = mensagem;
  faixa.classList.remove('sucesso', 'erro');
  faixa.classList.add(tipo);
  faixa.hidden = false;
}
