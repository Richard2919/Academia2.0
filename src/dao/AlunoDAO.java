package dao;

import banco.FabricaConexao;
import model.Aluno;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {

    public void salvar(Aluno aluno) {
        String sql = "INSERT INTO alunos(nome, data_vencimento) VALUES(?, ?)";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, aluno.getNome());
            pstmt.setString(2, aluno.getCpf());
            pstmt.setInt(3, aluno.getAltura()); // Enviando como centímetros (int)
            pstmt.setString(4, aluno.getDataVencimento().toString());

            pstmt.executeUpdate();
            System.out.println("✅ Aluno cadastrado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao salvar: " + e.getMessage());
        }
    }

    public List<Aluno> listarTodos() {
        List<Aluno> lista = new ArrayList<>();
        String sql = "SELECT * FROM alunos";
        try (Connection conn = FabricaConexao.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Aluno a = new Aluno();
                a.setId(rs.getInt("id"));
                a.setNome(rs.getString("nome"));
                a.setCpf(rs.getString("cpf")); // Adicione esta linha
                a.setAltura(rs.getInt("altura")); // Adicione esta linha
                a.setDataVencimento(LocalDate.parse(rs.getString("data_vencimento")));
                lista.add(a);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar: " + e.getMessage());
        }
        return lista;
    }

    // --- MÉTODO NOVO PARA RENOVAR MENSALIDADE ---
    public void renovarMensalidade(int idAluno) {
        // Regra de negócio: O novo vencimento será exatamente 1 mês a partir do dia de hoje
        LocalDate novoVencimento = LocalDate.now().plusMonths(1);

        // O comando UPDATE altera dados. O "WHERE id = ?" é VITAL para não alterar a data de todo mundo!
        String sql = "UPDATE alunos SET data_vencimento = ? WHERE id = ?";

        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, novoVencimento.toString());
            pstmt.setInt(2, idAluno);

            // O executeUpdate retorna o número de linhas que ele conseguiu alterar no banco
            int linhasAfetadas = pstmt.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("✅ Pagamento confirmado! Mensalidade renovada para: " + novoVencimento);
            } else {
                System.out.println("❌ Erro: Nenhum aluno encontrado com o ID " + idAluno);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar pagamento: " + e.getMessage());
        }
    }

    // --- MÉTODO NOVO PARA EXCLUIR ALUNO E SEUS DADOS EM CASCATA ---
    public void excluir(int idAluno) {
        // Passo 1: Descobre quais são as fichas do aluno e apaga os itens (exercícios) delas
        String sqlItens = "DELETE FROM itens_ficha WHERE ficha_id IN (SELECT id FROM fichas WHERE aluno_id = ?)";
        // Passo 2: Apaga as fichas vinculadas a esse aluno
        String sqlFichas = "DELETE FROM fichas WHERE aluno_id = ?";
        // Passo 3: Apaga o aluno
        String sqlAluno = "DELETE FROM alunos WHERE id = ?";

        try (Connection conn = FabricaConexao.getConexao()) {
            conn.setAutoCommit(false); // Inicia uma transação segura

            try (PreparedStatement pstmtItens = conn.prepareStatement(sqlItens);
                 PreparedStatement pstmtFichas = conn.prepareStatement(sqlFichas);
                 PreparedStatement pstmtAluno = conn.prepareStatement(sqlAluno)) {

                // 1. Apaga os itens
                pstmtItens.setInt(1, idAluno);
                pstmtItens.executeUpdate();

                // 2. Apaga as fichas
                pstmtFichas.setInt(1, idAluno);
                pstmtFichas.executeUpdate();

                // 3. Apaga o aluno
                pstmtAluno.setInt(1, idAluno);
                int linhasAfetadas = pstmtAluno.executeUpdate();

                if (linhasAfetadas > 0) {
                    conn.commit(); // Se o aluno existia e foi apagado, confirma tudo!
                    System.out.println("✅ Aluno e todo o seu histórico foram excluídos com sucesso!");
                } else {
                    conn.rollback(); // Se o aluno não existia, desfaz a operação
                    System.out.println("❌ Erro: Nenhum aluno encontrado com o ID " + idAluno);
                }

            } catch (SQLException ex) {
                conn.rollback(); // Em caso de erro no SQL, cancela e protege os dados
                System.err.println("Erro durante a exclusão. Operação desfeita: " + ex.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Erro de conexão: " + e.getMessage());
        }
    }
}