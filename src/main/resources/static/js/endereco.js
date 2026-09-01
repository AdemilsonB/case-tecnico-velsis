// Consulta o ViaCEP e preenche rua, cidade e estado a partir do CEP.
// A falha na consulta não pode impedir o cadastro: os campos continuam
// editáveis e o usuário digita o endereço manualmente.

async function buscarEndereco() {
  const cep = document.getElementById('zip').value.replace(/\D/g, '');

  if (cep.length !== 8) {
    return;
  }

  try {
    const resposta = await fetch('https://viacep.com.br/ws/' + cep + '/json/');

    if (!resposta.ok) {
      return;
    }

    const dados = await resposta.json();

    if (dados.erro) {
      return;
    }

    document.getElementById('addressLine').value = dados.logradouro || '';
    document.getElementById('city').value = dados.localidade || '';
    document.getElementById('state').value = dados.uf || '';
    document.getElementById('addressNumber').focus();
  } catch (falha) {
    // Serviço externo indisponível: segue com preenchimento manual
  }
}

// A máscara do CEP já é aplicada por validacao.js; aqui só a consulta
document.getElementById('zip').addEventListener('blur', buscarEndereco);
