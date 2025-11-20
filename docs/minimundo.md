📘 MiniMundo — Sistema de Agenda Telefónica Digital (ConnectMe)

A empresa ConnectMe deseja modernizar o seu processo de gestão de contactos, substituindo registos manuais e aplicações simples por um sistema digital robusto, eficiente e seguro. Atualmente, muitos contactos de clientes, parceiros e funcionários estão dispersos em ficheiros, folhas de cálculo e aplicações sem mecanismos avançados de pesquisa, o que dificulta a organização e o acesso rápido à informação.

O novo sistema de Agenda Telefónica Digital visa centralizar, automatizar e otimizar o armazenamento e a pesquisa de contactos, garantindo integridade, rapidez e facilidade de utilização. Além disso, pretende integrar conceitos de Estruturas de Dados e Algoritmos (EDA) para tornar o sistema escalável e eficiente.

👥 Gestão de Contactos

O sistema permitirá o cadastro de contactos pertencentes a pessoas físicas ou entidades. Cada contacto armazenado conterá:

Nome completo

Número de telefone

Endereço de email

Morada (opcional)

Para garantir consistência, o sistema aplica validações como:

Nome obrigatório

Email único

Número de telefone válido

Morada opcional, exceto em casos definidos pela empresa

Após o registo, o contacto passa a integrar as estruturas de dados utilizadas internamente pelo sistema, como listas ligadas, árvores de pesquisa binária e tabelas hash, permitindo operações rápidas e pesquisas otimizadas.

🔍 Pesquisa e Organização

Os contactos podem ser pesquisados por:

Nome

Número de telefone

As pesquisas utilizam algoritmos e estruturas, como:

Pesquisa linear

Pesquisa binária

Árvores Binárias de Pesquisa (BST)

Tabelas Hash

O sistema também permite ordenar contactos alfabeticamente através de algoritmos como:

QuickSort

MergeSort

BubbleSort

A ordenação é aplicada sobre listas dinâmicas, permitindo ao utilizador visualizar a agenda organizada conforme a sua preferência.

✏️ Operações Disponíveis (CRUD)

O sistema suporta todas as operações essenciais de gestão de contactos:

Inserção

Adiciona um novo contacto com as informações obrigatórias.

Valida duplicidade de email e telefone.

Leitura

Lista todos os contactos existentes.

Permite filtragem por pesquisa.

Atualização

Permite editar qualquer campo de um contacto já registado.

Remoção

Elimina um contacto específico.

Remove da base de dados e das estruturas em memória.

Todos os registos são armazenados em MySQL, e a camada DAO garante a comunicação segura via JDBC.

📤 Exportação e Importação

O sistema oferece mecanismos de exportação dos contactos filtrados por pesquisa. O utilizador poderá gerar ficheiros:

PDF, utilizando a biblioteca iText para documentos formatados e profissionais

TXT, utilizando escrita simples em ficheiros de texto

A exportação é baseada na filtragem atual, permitindo ao utilizador gerar relatórios precisos e customizados.

A importação também é suportada, permitindo carregar contactos a partir de ficheiros preparados previamente.

🔒 Segurança e Acesso

O acesso ao sistema é realizado por meio de:

Tela de Login

Autenticação por nome de utilizador e senha

Hashing das senhas com SHA-256 antes do armazenamento

Proteção contra tentativas incorretas consecutivas

Apenas utilizadores registados podem aceder ao sistema. Como o objetivo é exclusivo de gestão interna da agenda, não existem níveis de acesso diferenciados.

🖥️ Interfaces do Sistema

O sistema contará com as seguintes interfaces principais:

Login

Autenticação de utilizadores

Validação com hash SHA-256

Tela Principal

Acesso às funcionalidades da agenda

Menu de navegação (Listar, Adicionar, Procurar)

Listar Contactos

Visualização geral

Filtros

Exportação em PDF/TXT

Adicionar Contacto

Formulário simples com validação

Procurar Contacto

Pesquisa por nome ou número

Visualização individual ou por lista filtrada

Editar Contacto

Alteração de dados registados

Exportação

Exportar resultados da pesquisa para PDF ou TXT

📜 Registo e Auditoria

O sistema mantém histórico básico de:

Contactos adicionados ou removidos

Exportações realizadas

Tentativas de login inválidas