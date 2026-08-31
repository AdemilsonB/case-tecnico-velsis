// Validação de formulário compartilhada entre a tela de cadastro e a de edição:
// o case exige que as duas usem exatamente as mesmas regras e mensagens.
// As regras aqui espelham as anotações de UserRequest no backend.

function somenteDigitos(valor) {
  return (valor || '').replace(/\D/g, '');
}

// Aceita valor colado já formatado porque a máscara sempre parte dos dígitos
function mascaraCpf(valor) {
  const d = somenteDigitos(valor).slice(0, 11);
  return d
    .replace(/^(\d{3})(\d)/, '$1.$2')
    .replace(/^(\d{3})\.(\d{3})(\d)/, '$1.$2.$3')
    .replace(/^(\d{3})\.(\d{3})\.(\d{3})(\d)/, '$1.$2.$3-$4');
}

function mascaraCep(valor) {
  const d = somenteDigitos(valor).slice(0, 8);
  return d.replace(/^(\d{5})(\d)/, '$1-$2');
}

// Mesma regra do CpfValidator no backend: dois dígitos verificadores
function cpfValido(cpf) {
  const d = somenteDigitos(cpf);
  if (d.length !== 11) {
    return false;
  }
  if (/^(\d)\1{10}$/.test(d)) {
    return false;
  }
  return digitoVerificador(d, 9) === Number(d[9]) && digitoVerificador(d, 10) === Number(d[10]);
}

function digitoVerificador(cpf, quantidadeDigitos) {
  let soma = 0;
  let peso = quantidadeDigitos + 1;
  for (let i = 0; i < quantidadeDigitos; i++) {
    soma += Number(cpf[i]) * peso;
    peso--;
  }
  const resto = soma % 11;
  return resto < 2 ? 0 : 11 - resto;
}

function aplicarMascaras(form) {
  form.document.addEventListener('input', function () {
    this.value = mascaraCpf(this.value);
  });
  form.zip.addEventListener('input', function () {
    this.value = mascaraCep(this.value);
  });
}

// Monta o objeto no formato que a API espera: documento e CEP só com dígitos
function lerFormulario(form) {
  return {
    name: form.name.value.trim(),
    birthDate: form.birthDate.value,
    document: somenteDigitos(form.document.value),
    addressLine: form.addressLine.value.trim(),
    addressNumber: form.addressNumber.value.trim(),
    city: form.city.value.trim(),
    state: form.state.value,
    zip: somenteDigitos(form.zip.value)
  };
}

function preencherFormulario(form, usuario) {
  form.name.value = usuario.name;
  form.birthDate.value = usuario.birthDate;
  form.document.value = mascaraCpf(usuario.document);
  form.addressLine.value = usuario.addressLine;
  form.addressNumber.value = usuario.addressNumber;
  form.city.value = usuario.city;
  form.state.value = usuario.state;
  form.zip.value = mascaraCep(usuario.zip);
}

function validarFormulario(form) {
  const dados = lerFormulario(form);
  const erros = [];

  if (!dados.name) {
    erros.push({ field: 'name', message: 'Nome é obrigatório' });
  } else if (dados.name.length < 3 || dados.name.length > 120) {
    erros.push({ field: 'name', message: 'Nome deve ter entre 3 e 120 caracteres' });
  }

  if (!dados.birthDate) {
    erros.push({ field: 'birthDate', message: 'Data de nascimento é obrigatória' });
  } else if (dados.birthDate >= new Date().toISOString().slice(0, 10)) {
    erros.push({ field: 'birthDate', message: 'Data de nascimento não pode ser futura' });
  }

  if (!dados.document) {
    erros.push({ field: 'document', message: 'Documento é obrigatório' });
  } else if (!cpfValido(dados.document)) {
    erros.push({ field: 'document', message: 'CPF inválido' });
  }

  if (!dados.addressLine) {
    erros.push({ field: 'addressLine', message: 'Rua é obrigatória' });
  }
  if (!dados.addressNumber) {
    erros.push({ field: 'addressNumber', message: 'Número é obrigatório' });
  }
  if (!dados.city) {
    erros.push({ field: 'city', message: 'Cidade é obrigatória' });
  }
  if (!dados.state) {
    erros.push({ field: 'state', message: 'Estado é obrigatório' });
  }

  if (!dados.zip) {
    erros.push({ field: 'zip', message: 'CEP é obrigatório' });
  } else if (dados.zip.length !== 8) {
    erros.push({ field: 'zip', message: 'CEP deve ter 8 dígitos' });
  }

  return erros;
}

function limparErros(form) {
  form.querySelectorAll('.erro').forEach(function (campo) {
    campo.classList.remove('erro');
  });
  form.querySelectorAll('.mensagem-campo').forEach(function (span) {
    span.textContent = '';
  });
}

// Marca os campos com erro e leva o foco para o primeiro deles
function marcarErros(form, erros) {
  limparErros(form);
  erros.forEach(function (erro) {
    const campo = form.elements[erro.field];
    if (!campo) {
      return;
    }
    campo.classList.add('erro');
    const span = form.querySelector('[data-erro="' + erro.field + '"]');
    if (span) {
      span.textContent = erro.message;
    }
  });
  const primeiro = form.querySelector('.erro');
  if (primeiro) {
    primeiro.focus();
  }
}
