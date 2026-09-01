// Camada única de acesso à API REST. Toda resposta de erro do backend chega
// no mesmo formato, então o tratamento fica concentrado aqui.

const BASE = '/api/users';

// Erro com o corpo devolvido pelo backend, para a tela marcar os campos
class ApiError extends Error {
  constructor(status, corpo) {
    super(corpo && corpo.error ? corpo.error : 'Erro ao comunicar com o servidor.');
    this.status = status;
    this.fields = corpo && corpo.fields ? corpo.fields : [];
  }
}

async function enviar(url, opcoes) {
  let resposta;
  try {
    resposta = await fetch(url, opcoes);
  } catch (falha) {
    // Backend fora do ar ou rede indisponível: sem isso a tela ficaria branca
    throw new ApiError(0, { error: 'Não foi possível conectar ao servidor.' });
  }

  if (resposta.status === 204) {
    return null;
  }

  const corpo = await resposta.json().catch(() => null);
  if (!resposta.ok) {
    throw new ApiError(resposta.status, corpo);
  }
  return corpo;
}

const api = {
  listar(pagina, tamanho, termo) {
    const parametros = new URLSearchParams({ page: pagina, size: tamanho });
    if (termo) {
      parametros.append('termo', termo);
    }
    return enviar(`${BASE}?${parametros}`, { method: 'GET' });
  },

  buscar(id) {
    return enviar(`${BASE}/${id}`, { method: 'GET' });
  },

  criar(usuario) {
    return enviar(BASE, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(usuario)
    });
  },

  atualizar(id, usuario) {
    return enviar(`${BASE}/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(usuario)
    });
  }
};
