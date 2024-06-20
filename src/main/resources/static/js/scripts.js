document.addEventListener("DOMContentLoaded", function() {
    carregarProdutos();

    document.getElementById("produtoForm").addEventListener("submit", function(event) {
        event.preventDefault();
        salvarProduto();
    });
});

function carregarProdutos() {
    fetch("/produtos")
        .then(response => response.json())
        .then(produtos => {
            const tabela = document.querySelector("#produtosTabela tbody");
            tabela.innerHTML = "";
            produtos.forEach(produto => {
                const row = tabela.insertRow();
                row.innerHTML = `
                    <td>${produto.id}</td>
                    <td>${produto.nome}</td>
                    <td>${produto.preco}</td>
                    <td>${produto.quantidade}</td>
                    <td><img src="${produto.imagemUrl}" alt="${produto.nome}" width="50"></td>
                    <td>
                        <button class="btn btn-success btn-sm" onclick="editarProduto(${produto.id})">Editar</button>
                        <button class="btn btn-danger btn-sm" onclick="deletarProduto(${produto.id})">Deletar</button>
                    </td>
                `;
            });
        });
}

function salvarProduto() {
    const id = document.getElementById("produtoId").value;
    const nome = document.getElementById("nome").value;
    const preco = document.getElementById("preco").value;
    const quantidade = document.getElementById("quantidade").value;
    const imagemInput = document.getElementById("imagem");
    const produto = { nome, preco, quantidade };

    if (imagemInput.files.length > 0) {
        const formData = new FormData();
        formData.append("imagem", imagemInput.files[0]);

        fetch("/produtos/upload", {
            method: "POST",
            body: formData
        })
        .then(response => response.text())
        .then(imagemUrl => {
            produto.imagemUrl = imagemUrl;
            enviarProduto(id, produto);
        });
    } else {
        enviarProduto(id, produto);
    }
}

function enviarProduto(id, produto) {
    const url = id ? `/produtos/${id}` : "/produtos";
    const method = id ? "PUT" : "POST";

    fetch(url, {
        method: method,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(produto)
    })
    .then(response => response.json())
    .then(() => {
        carregarProdutos();
        document.getElementById("produtoForm").reset();
    });
}

function editarProduto(id) {
    fetch(`/produtos/${id}`)
        .then(response => response.json())
        .then(produto => {
            document.getElementById("produtoId").value = produto.id;
            document.getElementById("nome").value = produto.nome;
            document.getElementById("preco").value = produto.preco;
            document.getElementById("quantidade").value = produto.quantidade;
            document.getElementById("imagem").value = produto.imagemUrl;
        });
}

function deletarProduto(id) {
    fetch(`/produtos/${id}`, {
        method: "DELETE"
    })
    .then(() => {
        carregarProdutos();
    });
}
