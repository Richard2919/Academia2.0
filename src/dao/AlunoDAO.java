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
            pstmt.setString(2, aluno.getDataVencimento().toString()); // Salva como texto (ex: 2026-04-18)
            pstmt.executeUpdate();
            System.out.println("✅ Aluno cadastrado! Próximo vencimento: " + aluno.getDataVencimento());
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
                a.setDataVencimento(LocalDate.parse(rs.getString("data_vencimento"))); // Converte texto para Data
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
}