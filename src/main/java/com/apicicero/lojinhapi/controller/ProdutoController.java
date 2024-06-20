package com.apicicero.lojinhapi.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.apicicero.lojinhapi.entity.Produto;
import com.apicicero.lojinhapi.service.ProdutoService;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public List<Produto> listarTodos() {
        System.out.println("Recebida requisição para listar todos os produtos.");
        List<Produto> produtos = produtoService.listarTodos();
        System.out.println("Respondendo com todos os produtos.");
        return produtos;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        System.out.println("Recebida requisição para buscar produto por ID: " + id);
        Optional<Produto> produto = produtoService.buscarPorId(id);
        if (produto.isPresent()) {
            System.out.println("Produto encontrado, respondendo com o produto.");
            return ResponseEntity.ok(produto.get());
        } else {
            System.out.println("Produto não encontrado, respondendo com 404.");
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Produto salvar(@RequestBody Produto produto) {
        System.out.println("Recebida requisição para salvar um novo produto.");
        Produto produtoSalvo = produtoService.salvar(produto);
        System.out.println("Produto salvo com sucesso.");
        return produtoSalvo;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImagem(@RequestParam("imagem") MultipartFile file) {
        try {

            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            byte[] bytes = file.getBytes();
            Path path = uploadPath.resolve(file.getOriginalFilename());
            Files.write(path, bytes);
            System.out.println("Imagem salva com sucesso: " + path.toString());
            return ResponseEntity.ok("/uploads/" + file.getOriginalFilename());
        } catch (IOException e) {
            System.out.println("Falha ao fazer upload da imagem: " + e.getMessage());
            return ResponseEntity.status(500).body("Falha ao fazer upload da imagem.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @RequestBody Produto produto) {
        System.out.println("Recebida requisição para atualizar produto com ID: " + id);
        if (!produtoService.buscarPorId(id).isPresent()) {
            System.out.println("Produto não encontrado, respondendo com 404.");
            return ResponseEntity.notFound().build();
        }
        produto.setId(id);
        Produto produtoAtualizado = produtoService.salvar(produto);
        System.out.println("Produto atualizado com sucesso.");
        return ResponseEntity.ok(produtoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        System.out.println("Recebida requisição para deletar produto com ID: " + id);
        if (!produtoService.buscarPorId(id).isPresent()) {
            System.out.println("Produto não encontrado, respondendo com 404.");
            return ResponseEntity.notFound().build();
        }
        produtoService.deletar(id);
        System.out.println("Produto deletado com sucesso.");
        return ResponseEntity.noContent().build();
    }
}

//./mvnw clean install
//./mvnw spring-boot:run